package com.example.budgets.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.budgets.BudgetsUiState
import com.example.budgets.overallPercentage
import com.example.budgets.remainingBudget
import com.example.common.utils.CurrencyFormatter
import com.example.ui.theme.Border
import com.example.ui.theme.Expense
import com.example.ui.theme.Income
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.Surface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun BudgetSummaryCard(
    state: BudgetsUiState,
    modifier: Modifier = Modifier
) {
    val monthName = java.time.Month.of(state.selectedMonth)
        .getDisplayName(
            java.time.format.TextStyle.FULL,
            java.util.Locale.getDefault()
        )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Month label
            Text(
                text = "$monthName ${state.selectedYear}".uppercase(),
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                letterSpacing = 0.8.sp,
                color = TextSecondary
            )

            // Spent + total
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = CurrencyFormatter.formatWithSymbol(state.totalSpent),
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 36.sp,
                    letterSpacing = (-1.2).sp,
                    color = TextPrimary,
                    lineHeight = 36.sp
                )
                Text(
                    text = "of ${CurrencyFormatter.formatWithSymbol(state.totalBudget)}",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            // Progress bar
            BudgetProgressBar(
                percentage = state.overallPercentage,
                isOverBudget = state.totalSpent > state.totalBudget,
                height = 8.dp
            )

            // Meta row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append("${(state.overallPercentage * 100).toInt()}%")
                        }
                        withStyle(style = SpanStyle(color = TextSecondary)) {
                            append(" used")
                        }
                    },
                    fontFamily = InterFontFamily,
                    fontSize = 12.sp
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append(CurrencyFormatter.formatWithSymbol(state.remainingBudget))
                        }
                        withStyle(style = SpanStyle(color = TextSecondary)) {
                            append(" left")
                        }
                    },
                    fontFamily = InterFontFamily,
                    fontSize = 12.sp
                )
            }
        }
    }
}

// ── Reusable progress bar ──────────────────────────────────────────────────

@Composable
fun BudgetProgressBar(
    percentage: Float,
    isOverBudget: Boolean,
    height: Dp = 6.dp,
    modifier: Modifier = Modifier
) {
    val fillColor = if (isOverBudget) Expense else Income
    val clampedPct = percentage.coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(height))
            .background(Border)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(clampedPct)
                .clip(RoundedCornerShape(height))
                .background(fillColor)
        )
    }
}