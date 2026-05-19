package com.example.transactions.add.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.TransactionType
import com.example.ui.theme.Accent
import com.example.ui.theme.Income
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.PennywiseTheme
import com.example.ui.theme.Surface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HeroAmountCard(
    amountInput: String,
    transactionType: TransactionType,
    onQuickAmountPressed: (Double) -> Unit,
    onDigitPressed: (String) -> Unit,
    onDeletePressed: () -> Unit,
    onDecimalPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = if (transactionType == TransactionType.EXPENSE) {
        Accent
    } else {
        Income
    }

    val glowColor = if (transactionType == TransactionType.EXPENSE) {
        Color(0xFF7B61FF)
    } else {
        Color(0xFF00E5A0)
    }

    // Split amount into whole and cents
    val parts = amountInput.split(".")
    val whole = parts[0]
    val cents = parts.getOrNull(1)
    val showDecimal = amountInput.contains(".")

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        val widthPx = with(LocalDensity.current) { maxWidth.toPx() }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Surface)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            glowColor.copy(alpha = 0.25f),
                            Color.Transparent
                        ),
                        center = Offset(widthPx * 0.5f, 0f),
                        radius = widthPx * 0.7f
                    )
                )
                // Bottom right glow — always red, fixed
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFF4D6A).copy(alpha = 0.20f),
                            Color.Transparent
                        ),
                        center = Offset(widthPx * 1.1f, with(LocalDensity.current) { 200.dp.toPx() }),
                        radius = widthPx * 0.6f
                    )
                )
                .padding(horizontal = 20.dp, vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // AMOUNT label
            Text(
                text = "AMOUNT",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                letterSpacing = 1.2.sp,
                color = TextSecondary
            )

            // Amount display
            AmountDisplay(
                amountInput = amountInput,
                accentColor = accentColor,
                onDigitPressed = onDigitPressed,
                onDeletePressed = onDeletePressed,
                onDecimalPressed = onDecimalPressed
            )

            // Quick amount chips
            QuickAmountChips(
                transactionType = transactionType,
                onQuickAmountPressed = onQuickAmountPressed
            )
        }
    }
}

// ── Amount display ─────────────────────────────────────────────────────────

@Composable
private fun AmountDisplay(
    amountInput: String,
    accentColor: Color,
    onDigitPressed: (String) -> Unit,
    onDeletePressed: () -> Unit,
    onDecimalPressed: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Blinking cursor animation
    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 550, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor_blink"
    )

    // Split amount into whole and cents
    val parts = amountInput.split(".")
    val whole = parts[0]
    val cents = parts.getOrNull(1)
    val showDecimal = amountInput.contains(".")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                focusRequester.requestFocus()
                keyboardController?.show()
            },
        contentAlignment = Alignment.Center
    ) {
        // Hidden BasicTextField — handles actual keyboard input
        BasicTextField(
            value = amountInput,
            onValueChange = { newValue ->
                // Process each character change
                val oldLength = amountInput.length
                val newLength = newValue.length

                when {
                    // Delete pressed
                    newLength < oldLength -> onDeletePressed()
                    // New character added
                    newLength > oldLength -> {
                        val newChar = newValue.last().toString()
                        when {
                            newChar == "." -> onDecimalPressed()
                            newChar.all { it.isDigit() } -> onDigitPressed(newChar)
                        }
                    }
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            modifier = Modifier
                .size(1.dp) // invisible — just captures keyboard
                .focusRequester(focusRequester)
                .onFocusChanged { isFocused = it.isFocused },
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 1.sp,
                color = Color.Transparent
            ),
            cursorBrush = SolidColor(Color.Transparent)
        )

        // Visual amount display
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Currency symbol
            Text(
                text = "₦",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = accentColor,
                letterSpacing = (-1).sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Whole number
            Text(
                text = whole,
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Black,
                fontSize = 68.sp,
                color = TextPrimary,
                letterSpacing = (-3).sp,
                lineHeight = 68.sp
            )

            // Decimal + cents
            if (showDecimal) {
                Text(
                    text = ".${cents ?: ""}",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = TextSecondary,
                    letterSpacing = (-1).sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Blinking cursor
            Spacer(modifier = Modifier.width(2.dp))
            Box(
                modifier = Modifier
                    .padding(bottom = 6.dp)
                    .width(3.dp)
                    .height(56.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accentColor.copy(alpha = cursorAlpha))
            )
        }
    }

    // Auto-focus and show keyboard on first composition
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }
}

// ── Quick amount chips ─────────────────────────────────────────────────────

@Composable
private fun QuickAmountChips(
    transactionType: TransactionType,
    onQuickAmountPressed: (Double) -> Unit
) {
    val quickAmounts = listOf(500.0, 1000.0, 2000.0, 5000.0)

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        quickAmounts.forEach { amount ->
            QuickChip(
                label = "+ ₦${amount.toInt()}",
                onClick = { onQuickAmountPressed(amount) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(30.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(Color.White.copy(alpha = 0.06f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(50.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            color = TextPrimary
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0A0A0A
)
@Composable
private fun HeroAmountCardPreview() {
    PennywiseTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            HeroAmountCard(
                amountInput = "82.37",
                transactionType = TransactionType.EXPENSE,
                onQuickAmountPressed = {},
                onDigitPressed = {},
                onDecimalPressed = {},
                onDeletePressed = {}
            )
        }
    }
}