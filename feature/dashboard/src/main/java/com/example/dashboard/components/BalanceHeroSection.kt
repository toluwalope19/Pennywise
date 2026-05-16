package com.example.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.common.utils.CurrencyFormatter
import com.example.ui.theme.Expense
import com.example.ui.theme.Income
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.PennywiseTheme
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun BalanceHeroSection(
    totalBalance: Double,
    totalIncome: Double,
    totalExpense: Double,
    currencySymbol: String = "₦"
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Balance label + number + badge
        BalanceDisplay(
            totalBalance = totalBalance,
            totalIncome = totalIncome,
            currencySymbol = currencySymbol
        )

        // Income + Expense cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IncomeExpenseCard(
                label = "INCOME",
                amount = totalIncome,
                currencySymbol = currencySymbol,
                isIncome = true,
                modifier = Modifier.weight(1f)
            )
            IncomeExpenseCard(
                label = "EXPENSES",
                amount = totalExpense,
                currencySymbol = currencySymbol,
                isIncome = false,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun BalanceDisplay(
    totalBalance: Double,
    totalIncome: Double,
    currencySymbol: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        // TOTAL BALANCE label
        Text(
            text = "TOTAL BALANCE",
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            letterSpacing = 0.8.sp,
            color = TextSecondary
        )

        // Large balance number — whole + cents split
        val balanceParts = String.format("%.2f", totalBalance).split(".")
        val whole = balanceParts[0]
        val cents = balanceParts.getOrElse(1) { "00" }

        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "$currencySymbol$whole",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 42.sp,
                letterSpacing = (-1.5).sp,
                color = TextPrimary,
                lineHeight = 42.sp
            )
            Text(
                text = ".$cents",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                letterSpacing = (-0.5).sp,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 5.dp)
            )
        }

        // Month change badge
        Surface(
            shape = RoundedCornerShape(50.dp),
            color = Income.copy(alpha = 0.12f)
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = 10.dp,
                    vertical = 4.dp
                ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowUpward,
                    contentDescription = null,
                    tint = Income,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = "+${CurrencyFormatter.formatWithSymbol(totalIncome)} this month",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    color = Income
                )
            }
        }
    }
}

@Composable
private fun IncomeExpenseCard(
    label: String,
    amount: Double,
    currencySymbol: String,
    isIncome: Boolean,
    modifier: Modifier = Modifier
) {
    val accentColor = if (isIncome) Income else Expense
    val arrowIcon = if (isIncome) Icons.Rounded.ArrowUpward else Icons.Rounded.ArrowDownward
    val amountPrefix = if (isIncome) "+" else "-"

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.10f),
                        accentColor.copy(alpha = 0.04f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.12f),
                        Color.White.copy(alpha = 0.03f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 14.dp, vertical = 14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon — bigger, rounded square, on the LEFT
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = if (isIncome) {
                                listOf(Color(0xFF00E5A0), Color(0xFF0BA67B))
                            } else {
                                listOf(Color(0xFFFF6580), Color(0xFFFF4D6A))
                            }
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = arrowIcon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Label + amount stacked in Column on the RIGHT
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = label,
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    letterSpacing = 0.6.sp,
                    color = TextSecondary
                )
                Text(
                    text = "$amountPrefix${CurrencyFormatter.formatWithSymbol(amount)}",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = (-0.3).sp,
                    color = accentColor,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0A0A0A
)
@Composable
private fun BalanceHeroPreview() {
    PennywiseTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            BalanceHeroSection(
                totalBalance = 8247.32,
                totalIncome = 2092.0,
                totalExpense = 671.0
            )
        }
    }
}
