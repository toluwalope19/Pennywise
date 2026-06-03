package com.example.transactions.mlkit


import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import java.time.LocalDate
import kotlin.collections.get
import kotlin.compareTo
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// ── ML Kit OCR ─────────────────────────────────────────────────────────────

suspend fun recognizeReceiptText(
    context: Context,
    imageUri: Uri
): String = suspendCancellableCoroutine { continuation ->
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    val image = InputImage.fromFilePath(context, imageUri)

    recognizer.process(image)
        .addOnSuccessListener { visionText ->
            android.util.Log.d("ML_KIT_OCR", "Raw text:\n${visionText.text}")
            continuation.resume(visionText.text)
        }
        .addOnFailureListener { e ->
            continuation.resumeWithException(e)
        }
}

// ── Receipt Parser ─────────────────────────────────────────────────────────

data class ReceiptResult(
    val amount: Double?,
    val merchant: String?,
    val date: LocalDate?
)

fun parseReceipt(rawText: String): ReceiptResult {
    val lines = rawText.lines()
        .map { it.trim() }
        .filter { it.isNotBlank() }

    // ── Amount parsing ─────────────────────────────────────────────────────

    // Extracts monetary amounts embedded anywhere in a line.
    // Handles: "₦5,001.00", "NGN 5,001", "Total: 19.50", "16.5", "14,30" (European decimal comma)
    val moneyBody = """\d{1,3}(?:[,\s]\d{3})+(?:[.,]\d{1,2})?|\d+[.,]\d{1,2}|\d{2,9}"""
    val amountWithCurrencyRegex = Regex(
        """(?i)(?:[$€£₦¥]|ngn|usd|eur|gbp)\s*($moneyBody)|($moneyBody)\s*(?:[$€£₦¥]|ngn|usd|eur|gbp)"""
    )
    val decimalOrGroupedAmountRegex = Regex("""\b(\d{1,3}(?:[,\s]\d{3})+(?:[.,]\d{1,2})?|\d+[.,]\d{1,2})\b""")
    val wholeNumberRegex = Regex("""\b\d{1,9}\b""")
    val currencyOnlyRegex = Regex("""(?i)^(?:[$€£₦¥]|ngn|usd|eur|gbp)$""")
    val paymentKeywords = setOf("cash", "change", "paid", "tip", "gratuity", "tender", "payment")

    data class AmountCandidate(
        val value: Double,
        val start: Int,
        val end: Int,
        val hasCurrency: Boolean = false,
        val hasDecimalOrGrouping: Boolean = false
    )

    fun parseAmount(raw: String): Double? {
        val compact = raw.replace(" ", "")
        val normalized = when {
            compact.contains('.') -> compact.replace(",", "")
            compact.count { it == ',' } == 1 && compact.substringAfter(',').length == 2 ->
                compact.replace(",", ".")
            else -> compact.replace(",", "")
        }
        return normalized.toDoubleOrNull()?.takeIf { it > 0 }
    }

    fun amountCandidates(line: String, allowWholeNumber: Boolean = false): List<AmountCandidate> {
        val candidates = buildList {
            amountWithCurrencyRegex.findAll(line).forEach { match ->
                val raw = match.groupValues[1].ifBlank { match.groupValues[2] }
                parseAmount(raw)?.let {
                    add(AmountCandidate(it, match.range.first, match.range.last + 1, hasCurrency = true))
                }
            }
            decimalOrGroupedAmountRegex.findAll(line).forEach { match ->
                parseAmount(match.groupValues[1])?.let {
                    add(
                        AmountCandidate(
                            it,
                            match.range.first,
                            match.range.last + 1,
                            hasDecimalOrGrouping = true
                        )
                    )
                }
            }
            if (allowWholeNumber) {
                wholeNumberRegex.findAll(line).forEach { match ->
                    parseAmount(match.value)?.let {
                        add(AmountCandidate(it, match.range.first, match.range.last + 1))
                    }
                }
            }
        }

        return candidates
            .distinctBy { "${it.start}-${it.end}-${it.value}" }
            .sortedBy { it.start }
    }

    fun extractLineAmount(line: String, allowWholeNumber: Boolean = false): Double? {
        val candidates = amountCandidates(line, allowWholeNumber)
        return candidates
            .filter { it.hasCurrency || it.hasDecimalOrGrouping }
            .lastOrNull()
            ?.value
            ?: candidates.lastOrNull()?.value
    }

    fun extractAmountForLabel(line: String, labelStart: Int, allowWholeNumber: Boolean = true): Double? {
        val candidates = amountCandidates(line, allowWholeNumber)
        if (candidates.isEmpty()) return null

        val lower = line.lowercase()
        val labelEnd = lower.indexOfAny(listOf(":", "="), startIndex = labelStart).takeIf { it != -1 }
            ?: lower.indexOf("total", startIndex = labelStart).takeIf { it != -1 }?.let { it + "total".length }
            ?: labelStart
        val paymentStart = paymentKeywords
            .mapNotNull { keyword ->
                lower.indexOf(keyword, startIndex = labelEnd).takeIf { it != -1 }
            }
            .minOrNull()

        val eligibleCandidates = candidates
            .filter { it.start >= labelEnd && (paymentStart == null || it.start < paymentStart) }

        return eligibleCandidates
            .maxWithOrNull(
                compareBy<AmountCandidate> { it.hasCurrency }
                    .thenBy { it.hasDecimalOrGrouping }
                    .thenBy { it.value }
            )
            ?.value
            ?: extractLineAmount(line, allowWholeNumber)
    }

    fun extractAmountBelowLabel(
        lines: List<String>,
        labelIndex: Int,
        maxLookAhead: Int = 3
    ): Double? {
        val lastIndex = minOf(lines.lastIndex, labelIndex + maxLookAhead)
        for (idx in (labelIndex + 1)..lastIndex) {
            val line = lines[idx]
            val lower = line.lowercase()
            if (paymentKeywords.any { lower.contains(it) }) break
            if (currencyOnlyRegex.matches(line.trim())) continue
            if (lower.contains("subtotal") || lower.contains("sub total") || lower.contains("sub-total")) break
            if (lower.contains("tax") || lower.contains("vat")) continue

            extractLineAmount(line, allowWholeNumber = true)?.let { return it }
        }
        return null
    }

    fun extractColumnAlignedTotal(lines: List<String>): Double? {
        val totalIndex = lines.indexOfLast { line ->
            val lower = line.lowercase()
            lower.contains("total") &&
                !lower.contains("subtotal") && !lower.contains("sub total") && !lower.contains("sub-total")
        }
        if (totalIndex == -1) return null

        val paymentLabels = buildList {
            add("total")
            lines.drop(totalIndex + 1)
                .takeWhile { line ->
                    val lower = line.lowercase()
                    !lower.contains("price") &&
                        !lower.contains("thank") &&
                        !lower.contains("approval") &&
                        !lower.contains("bank")
                }
                .forEach { line ->
                    val lower = line.lowercase()
                    when {
                        lower.contains("cash") -> add("cash")
                        lower.contains("change") -> add("change")
                        lower.contains("paid") -> add("paid")
                        lower.contains("tender") -> add("tender")
                    }
                }
        }
        if (paymentLabels.size < 2) return null

        val decimalColumn = lines.drop(totalIndex + 1)
            .mapNotNull { line ->
                val trimmed = line.trim()
                if (amountCandidates(trimmed).isNotEmpty() && trimmed.all { it.isDigit() || it == '.' || it == ',' }) {
                    extractLineAmount(trimmed)
                } else {
                    null
                }
            }
        if (decimalColumn.size < paymentLabels.size) return null

        return decimalColumn
            .take(decimalColumn.size - paymentLabels.size + 1)
            .lastOrNull()
    }

    val cashLineIndex = lines.indexOfFirst { line ->
        paymentKeywords.any { line.lowercase().trim() == it }
    }
    val safeLines = if (cashLineIndex != -1) lines.take(cashLineIndex) else lines

    var amount: Double? = null

    // Priority 1 — strong total labels, bottom-up (last match = actual total)
    val highPriorityKeywords = listOf(
        "grand total", "total amount", "amount due", "balance due", "total due", "net total"
    )
    outer@ for (keyword in highPriorityKeywords) {
        val idx = safeLines.indexOfLast { it.lowercase().contains(keyword) }
        if (idx != -1) {
            val labelStart = safeLines[idx].lowercase().lastIndexOf(keyword)
            amount = extractAmountForLabel(safeLines[idx], labelStart)
                ?: extractAmountBelowLabel(safeLines, idx)
            if (amount != null) break@outer
        }
    }

    // Priority 2 — "total" / "to pay", explicitly excluding subtotal lines
    if (amount == null) {
        val idx = safeLines.indexOfLast { line ->
            val lower = line.lowercase()
            (lower.contains("total") || lower.contains("to pay")) &&
                !lower.contains("subtotal") && !lower.contains("sub total") && !lower.contains("sub-total")
        }
        if (idx != -1) {
            val lower = safeLines[idx].lowercase()
            val labelStart = maxOf(lower.lastIndexOf("total"), lower.lastIndexOf("to pay"))
            amount = extractAmountForLabel(safeLines[idx], labelStart)
                ?: extractAmountBelowLabel(safeLines, idx)
        }
    }

    // Priority 3 — subtotal only when no "total" line was found
    if (amount == null) {
        val idx = safeLines.indexOfLast { line ->
            val lower = line.lowercase()
            lower.contains("subtotal") || lower.contains("sub total") || lower.contains("sub-total")
        }
        if (idx != -1) {
            amount = extractLineAmount(safeLines[idx], allowWholeNumber = true)
                ?: extractAmountBelowLabel(safeLines, idx)
        }
    }

    // Fallback for OCR column ordering: labels first, numeric column later.
    if (amount == null) {
        amount = extractColumnAlignedTotal(lines)
    }

    // Fallback — largest monetary value in safe lines
    if (amount == null) {
        amount = safeLines.mapNotNull { extractLineAmount(it) }.maxOrNull()
    }

    // ── Merchant parsing ───────────────────────────────────────────────────
    val skipWords = setOf(
        "receipt", "invoice", "tax", "vat", "welcome", "thank", "please",
        "call", "visit", "www", "tel", "phone", "date", "time", "no.",
        "copy", "customer", "original", "cash", "change", "approval",
        "description", "price", "bank", "card", "designed", "lorem",
        "ipsum", "address", "telp", "freepik", "total", "subtotal"
    )

    val merchant = lines.firstOrNull { line ->
        line.length in 3..40 &&
                !line.all { it.isDigit() || it == '/' || it == ':' || it == '-' || it == '*' || it == '.' || it == '#' } &&
                !skipWords.any { skip -> line.lowercase().contains(skip) } &&
                !line.contains(Regex("""\d{4,}""")) &&
                !line.contains("*") &&
                line.replace(",", ".").toDoubleOrNull() == null
    }

    // ── Date parsing ───────────────────────────────────────────────────────
    val datePatterns = listOf(
        Regex("""(\d{1,2})[/\-](\d{1,2})[/\-](\d{4})"""),
        Regex("""(\d{4})[/\-](\d{2})[/\-](\d{2})"""),
        Regex("""(\d{1,2})\s+(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\w*\s+(\d{4})""", RegexOption.IGNORE_CASE),
        Regex("""(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\w*\s+(\d{1,2}),?\s+(\d{4})""", RegexOption.IGNORE_CASE)
    )

    val monthMap = mapOf(
        "jan" to 1, "feb" to 2, "mar" to 3, "apr" to 4,
        "may" to 5, "jun" to 6, "jul" to 7, "aug" to 8,
        "sep" to 9, "oct" to 10, "nov" to 11, "dec" to 12
    )

    var parsedDate: LocalDate? = null
    for (pattern in datePatterns) {
        val match = pattern.find(rawText) ?: continue
        try {
            parsedDate = when {
                match.groupValues[1].length == 4 ->
                    LocalDate.of(
                        match.groupValues[1].toInt(),
                        match.groupValues[2].toInt(),
                        match.groupValues[3].toInt()
                    )
                match.groupValues[2].length >= 3 &&
                        match.groupValues[2].lowercase().take(3) in monthMap ->
                    LocalDate.of(
                        match.groupValues[3].toInt(),
                        monthMap[match.groupValues[2].lowercase().take(3)]!!,
                        match.groupValues[1].toInt()
                    )
                match.groupValues[1].length >= 3 &&
                        match.groupValues[1].lowercase().take(3) in monthMap ->
                    LocalDate.of(
                        match.groupValues[3].toInt(),
                        monthMap[match.groupValues[1].lowercase().take(3)]!!,
                        match.groupValues[2].toInt()
                    )
                else -> {
                    val day = match.groupValues[1].toInt()
                    val month = match.groupValues[2].toInt()
                    val year = match.groupValues[3].toInt()
                    if (month in 1..12 && day in 1..31)
                        LocalDate.of(year, month, day)
                    else null
                }
            }
            if (parsedDate != null) break
        } catch (_: Exception) { continue }
    }

    return ReceiptResult(
        amount = amount,
        merchant = merchant,
        date = parsedDate
    )
}
