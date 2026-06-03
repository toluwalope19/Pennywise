package com.example.dashboard.components


import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.PieChart
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.common.utils.CurrencyFormatter
import com.example.dashboard.CategorySpending
import com.example.ui.components.CategoryType
import com.example.ui.components.DonutChart
import com.example.ui.components.DonutSegment
import com.example.ui.theme.Income
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.PennywiseTheme
import com.example.ui.theme.Surface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary


@Composable
fun DonutCard(
    totalExpense: Double,
    categorySpending: List<CategorySpending>,
    currencySymbol: String = "₦",
    dotLegend: Boolean = false,
    sideLegend: Boolean = false
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.04f),
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        val widthPx = with(LocalDensity.current) { maxWidth.toPx() }
        val heightPx = with(LocalDensity.current) { maxHeight.toPx() }

        val segments = categorySpending.map { spending ->
            DonutSegment(
                value = spending.percentage,
                color = CategoryType.fromName(spending.categoryName).gradientStart,
                label = spending.categoryName
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Surface)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF7B61FF).copy(alpha = 0.18f),
                            Color.Transparent
                        ),
                        center = Offset(
                            x = widthPx * 0.50f,
                            y = heightPx * 0.38f
                        ),
                        radius = widthPx * 0.55f
                    )
                )
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (sideLegend && categorySpending.isNotEmpty()) {
                // Donut left, single-column legend right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DonutChart(
                        segments = segments,
                        chartSize = 200.dp,
                        strokeWidth = 22.dp,
                        centerContent = {
                            DonutCenterContent(
                                totalExpense = totalExpense,
                                currencySymbol = currencySymbol
                            )
                        }
                    )
                    CategoryLegendGrid(
                        categorySpending = categorySpending,
                        singleColumn = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                // Default: chart on top, legend below
                DonutChart(
                    segments = segments,
                    chartSize = 240.dp,
                    strokeWidth = 24.dp,
                    centerContent = {
                        DonutCenterContent(
                            totalExpense = totalExpense,
                            currencySymbol = currencySymbol
                        )
                    }
                )
                if (categorySpending.isNotEmpty()) {
                    if (dotLegend) {
                        CategoryDotLegend(categorySpending = categorySpending)
                    } else {
                        CategoryLegendGrid(categorySpending = categorySpending)
                    }
                }
            }
        }
    }
}

@Composable
private fun DonutCenterContent(
    totalExpense: Double,
    currencySymbol: String
) {
    val isEmpty = totalExpense <= 0.0

    val animatedExpense by animateFloatAsState(
        targetValue = totalExpense.toFloat(),
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = "expense_counter"
    )

    if (isEmpty) {
        // Empty state center
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.PieChart,
                contentDescription = null,
                tint = TextSecondary.copy(alpha = 0.4f),
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = "No expenses",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = TextSecondary.copy(alpha = 0.6f)
            )
            Text(
                text = "this month",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                color = TextSecondary.copy(alpha = 0.4f)
            )
        }
    } else {
        // Normal state — existing content
        val formatted = CurrencyFormatter.format(animatedExpense.toDouble())
        val parts = formatted.split(".")
        val whole = parts[0]
        val cents = parts.getOrElse(1) { "00" }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "TOTAL EXPENSES",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                letterSpacing = 1.2.sp,
                color = TextSecondary
            )
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$currencySymbol$whole",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 32.sp,
                    letterSpacing = (-1.2).sp,
                    color = TextPrimary,
                    lineHeight = 32.sp
                )
                Text(
                    text = ".$cents",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }
            Surface(
                shape = RoundedCornerShape(50.dp),
                color = Income.copy(alpha = 0.12f)
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = 8.dp,
                        vertical = 3.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowDownward,
                        contentDescription = null,
                        tint = Income,
                        modifier = Modifier.size(11.dp)
                    )
                    Text(
                        text = "12% vs last month",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        color = Income
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0A0A0A
)
@Composable
private fun DonutCardPreview() {
    PennywiseTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            DonutCard(
                totalExpense = 671.89,
                categorySpending = listOf(
                    CategorySpending("FOOD", 214.84, 32f),
                    CategorySpending("HEALTH", 147.82, 22f),
                    CategorySpending("TRANSPORT", 107.50, 16f),
                    CategorySpending("SHOPPING", 94.06, 14f),
                    CategorySpending("EDUCATION", 60.47, 9f),
                    CategorySpending("OTHER", 47.20, 7f)
                )
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0A0A0A
)
@Composable
private fun DonutCardEmptyPreview() {
    PennywiseTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            DonutCard(
                totalExpense = 0.0,
                categorySpending = emptyList()
            )
        }
    }
}