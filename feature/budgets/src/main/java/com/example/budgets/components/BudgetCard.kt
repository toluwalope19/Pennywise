package com.example.budgets.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.budgets.BudgetWithSpending
import com.example.budgets.BudgetsUiEvent
import com.example.common.utils.CurrencyFormatter
import com.example.ui.components.CategoryDisplay
import com.example.ui.components.toDisplay
import com.example.ui.theme.Expense
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.Surface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary


@Composable
fun BudgetCard(
    budgetWithSpending: BudgetWithSpending,
    onEvent: (BudgetsUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryDisplay = budgetWithSpending.category?.toDisplay()
        ?: CategoryDisplay(
            name = "Other",
            icon = Icons.Rounded.MoreHoriz,
            color = Color(0xFF8C8C8C)
        )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.04f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable {
                onEvent(BudgetsUiEvent.OnBudgetClick(budgetWithSpending.budget.id))
            }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header row — icon + name + amounts
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Category icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(categoryDisplay.color),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = categoryDisplay.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Name
                Text(
                    text = categoryDisplay.name,
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )

                // Spent / limit
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = CurrencyFormatter.formatWithSymbol(
                            budgetWithSpending.spent
                        ),
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = if (budgetWithSpending.isOverBudget) {
                            Expense
                        } else {
                            TextPrimary
                        }
                    )
                    Text(
                        text = "/ ${CurrencyFormatter.formatWithSymbol(
                            budgetWithSpending.budget.amount
                        )}",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }

            // Progress bar
            BudgetProgressBar(
                percentage = budgetWithSpending.percentage,
                isOverBudget = budgetWithSpending.isOverBudget,
                height = 6.dp
            )

            // Over budget note
            if (budgetWithSpending.isOverBudget) {
                val overspend = budgetWithSpending.spent - budgetWithSpending.budget.amount
                Text(
                    text = "↑ ${CurrencyFormatter.formatWithSymbol(overspend)} over budget",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = Expense
                )
            }
        }
    }
}