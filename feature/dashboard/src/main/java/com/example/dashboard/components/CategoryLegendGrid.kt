package com.example.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.common.utils.CurrencyFormatter
import com.example.dashboard.CategorySpending
import com.example.ui.components.CategoryType
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.PennywiseTheme
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CategoryLegendGrid(
    categorySpending: List<CategorySpending>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categorySpending.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { spending ->
                    LegendCard(
                        spending = spending,
                        modifier = Modifier.weight(1f)
                    )
                }
                // If odd number — fill empty slot so card doesn't stretch
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

// ── Legend card ────────────────────────────────────────────────────────────

@Composable
private fun LegendCard(
    spending: CategorySpending,
    modifier: Modifier = Modifier
) {
    val categoryType = CategoryType.fromName(spending.categoryName)
    val categoryColor = categoryType.gradientStart

    // Format category name — first letter uppercase only
    val displayName = spending.categoryName
        .lowercase()
        .replaceFirstChar { it.uppercase() }


    Box(
        modifier = modifier
            .height(65.dp) // fixed height — uniform across all cards
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.03f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 10.dp, vertical = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Thin colored vertical bar — left edge
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(30.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(categoryColor)
            )

            // Category name + amount — takes all remaining space
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = displayName,
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = LocalTextStyle.current.copy(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    )
                )
                Text(
                    text = CurrencyFormatter.formatWithSymbol(spending.amount),
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.offset(y = (-5).dp)
                )
            }

            // Percentage — right-aligned in category color
            Text(
                text = "${spending.percentage.toInt()}%",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = categoryColor
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0A0A0A
)
@Composable
private fun CategoryLegendGridPreview() {
    PennywiseTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            CategoryLegendGrid(
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