package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.TransactionType
import com.example.ui.theme.Accent
import com.example.ui.theme.Income
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.PennywiseTheme
import com.example.ui.theme.Surface
import com.example.ui.theme.TextSecondary

@Composable
fun HeroAmountCard(
    amountInput: String,
    transactionType: TransactionType,
    onQuickAmountPressed: (Double) -> Unit,
    onDigitPressed: (String) -> Unit,
    onDeletePressed: () -> Unit,
    onDecimalPressed: () -> Unit,
    modifier: Modifier = Modifier,
    quickChips: List<QuickAmountChip> = listOf(
        QuickAmountChip("+ ₦500", 500.0),
        QuickAmountChip("+ ₦1,000", 1000.0),
        QuickAmountChip("+ ₦2,000", 2000.0),
        QuickAmountChip("+ ₦5,000", 5000.0)
    )
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
                chips = quickChips,
                onQuickAmountPressed = onQuickAmountPressed
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0A0A0A
)
@Composable
private fun HeroAmountCardExpensePreview() {
    PennywiseTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            HeroAmountCard(
                amountInput = "82.37",
                transactionType = TransactionType.EXPENSE,
                onDigitPressed = {},
                onDeletePressed = {},
                onDecimalPressed = {},
                onQuickAmountPressed = {}
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0A0A0A
)
@Composable
private fun HeroAmountCardBudgetPreview() {
    PennywiseTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            HeroAmountCard(
                amountInput = "500",
                transactionType = TransactionType.EXPENSE,
                onDigitPressed = {},
                onDeletePressed = {},
                onDecimalPressed = {},
                onQuickAmountPressed = {},
                quickChips = listOf(
                    QuickAmountChip("₦100", 100.0),
                    QuickAmountChip("₦250", 250.0),
                    QuickAmountChip("₦500", 500.0),
                    QuickAmountChip("₦1,000", 1000.0)
                )
            )
        }
    }
}