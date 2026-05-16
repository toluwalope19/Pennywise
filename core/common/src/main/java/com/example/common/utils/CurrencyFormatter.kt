package com.example.common.utils



import java.text.NumberFormat
import java.util.Locale



object CurrencyFormatter {

    private val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    fun format(amount: Double): String {
        return when {
            amount >= 1_000_000 -> String.format("%.1fM", amount / 1_000_000)
            amount >= 100_000 -> String.format("%.1fK", amount / 1_000)
            else -> formatter.format(amount) // full commas + 2dp below 100k
        }
    }

    fun formatWithSymbol(amount: Double, symbol: String = "₦"): String {
        return "$symbol${format(amount)}"
    }
}