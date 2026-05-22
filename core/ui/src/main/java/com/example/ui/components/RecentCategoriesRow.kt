package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CategoryType
import com.example.ui.theme.Accent
import com.example.ui.theme.Border
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.PennywiseTheme
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun RecentCategoriesRow(
    recentCategories: List<CategoryType>,
    selectedCategory: CategoryType,
    onCategorySelected: (CategoryType) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Section label
        Text(
            text = "RECENT",
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            letterSpacing = 1.sp,
            color = TextSecondary,
            modifier = Modifier.padding(
                start = 20.dp,
                end = 20.dp,
                bottom = 10.dp
            )
        )

        // Horizontal scrollable chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            items(recentCategories) { category ->
                RecentCategoryChip(
                    category = category,
                    isSelected = selectedCategory == category,
                    onClick = { onCategorySelected(category) }
                )
            }
        }
    }
}

@Composable
private fun RecentCategoryChip(
    category: CategoryType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .height(38.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(SurfaceElevated)
            .border(
                width = 1.dp,
                color = if (isSelected) Accent.copy(alpha = 0.5f) else Border,
                shape = RoundedCornerShape(50.dp)
            )
            .clickable { onClick() }
            .padding(start = 8.dp, end = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Mini gradient icon
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            category.gradientStart,
                            category.gradientEnd
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
        Text(
            text = category.displayName,
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = TextPrimary
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF141414
)
@Composable
private fun RecentCategoryChipPreview() {
    PennywiseTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                RecentCategoryChip(
                    category = CategoryType.FOOD,
                    isSelected = true,
                    onClick = {}
                )
                RecentCategoryChip(
                    category = CategoryType.TRANSPORT,
                    isSelected = false,
                    onClick = {}
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF141414
)
@Composable
private fun RecentCategoriesRowPreview() {
    PennywiseTheme {
        RecentCategoriesRow(
            recentCategories = listOf(
                CategoryType.FOOD,
                CategoryType.TRANSPORT,
                CategoryType.SHOPPING,
                CategoryType.UTILITIES
            ),
            selectedCategory = CategoryType.FOOD,
            onCategorySelected = {}
        )
    }
}