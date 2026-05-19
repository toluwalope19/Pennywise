package com.example.transactions.add.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Category
import com.example.ui.components.CategoryIcon
import com.example.ui.components.CategoryIconShape
import com.example.ui.components.toDisplay
import com.example.ui.theme.Accent
import com.example.ui.theme.Border
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.PennywiseTheme
import com.example.ui.theme.Surface
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

// ── Category grid ──────────────────────────────────────────────────────────

@Composable
fun CategoryGrid(
    categories: List<Category>,
    selectedCategoryId: Long,
    onCategorySelected: (Category) -> Unit,
    onAddNew: () -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(
            end = 16.dp,
            start = 16.dp,
            bottom = 28.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(categories) { category ->
            CategoryCell(
                category = category,
                isSelected = category.id == selectedCategoryId,
                onClick = { onCategorySelected(category) }
            )
        }
        item {
            AddNewCategoryCell(onClick = onAddNew)
        }
    }
}

// ── Category cell ──────────────────────────────────────────────────────────

@Composable
private fun CategoryCell(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val display = category.toDisplay()

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .then(
                if (isSelected) {
                    Modifier
                        .background(Accent.copy(alpha = 0.08f))
                        .border(
                            width = 1.5.dp,
                            color = Accent,
                            shape = RoundedCornerShape(14.dp)
                        )
                } else {
                    Modifier.background(Color.Transparent)
                }
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box {
            CategoryIcon(
                display = display,
                size = 52.dp,
                shape = CategoryIconShape.ROUNDED_SQUARE
            )
            if (isSelected) {
                CheckmarkBadge(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-4).dp)
                )
            }
        }
        Text(
            text = display.name,
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

// ── Checkmark badge ────────────────────────────────────────────────────────

@Composable
private fun CheckmarkBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(18.dp)
            .clip(CircleShape)
            .background(Accent)
            .border(
                width = 2.dp,
                color = Surface,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.Check,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(10.dp)
        )
    }
}

// ── Add new category cell ──────────────────────────────────────────────────

@Composable
private fun AddNewCategoryCell(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceElevated)
                .border(
                    width = 1.5.dp,
                    color = Border,
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "Add category",
                tint = TextSecondary,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = "New",
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            color = TextSecondary
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF141414
)
@Composable
private fun CategoryCellPreview() {
    PennywiseTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CategoryCell(
                category = Category(1, "Food", "restaurant", "#FF8A3D"),
                isSelected = false,
                onClick = {}
            )
            CategoryCell(
                category = Category(3, "Health", "fitness_center", "#5AE9C8"),
                isSelected = true,
                onClick = {}
            )
            AddNewCategoryCell(onClick = {})
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF141414
)
@Composable
private fun CategoryGridPreview() {
    PennywiseTheme {
        CategoryGrid(
            categories = listOf(
                Category(1, "Food", "restaurant", "#FF8A3D"),
                Category(2, "Shopping", "shopping_bag", "#FF7AC1"),
                Category(3, "Health", "fitness_center", "#5AE9C8"),
                Category(4, "Transport", "directions_car", "#4FD1FF"),
            ),
            selectedCategoryId = 3L,
            onCategorySelected = {},
            onAddNew = {}
        )
    }
}