package com.example.transactions.add.components


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Category
import com.example.ui.components.CategoryType
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.PennywiseTheme
import com.example.ui.theme.Surface
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryPickerSheet(
    categories: List<Category>,
    selectedCategory: CategoryType,
    onCategorySelected: (CategoryType) -> Unit,
    onDismiss: () -> Unit,
    onCategoryCreated: (name: String, color: Color, icon: ImageVector) -> Unit
) {

    var showCreateCategory by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }

    val recentCategories = remember {
        listOf(
            CategoryType.FOOD,
            CategoryType.TRANSPORT,
            CategoryType.SHOPPING,
            CategoryType.UTILITIES
        )
    }

    val displayCategories = categories.map { category ->
        CategoryType.fromName(category.name)
    }


    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            // Handle pill
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 6.dp)
                    .width(32.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color.White.copy(alpha = 0.2f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            SheetHeader(onDismiss = onDismiss)

            SheetSearchBar(
                query = searchQuery,
                onQueryChanged = { searchQuery = it }
            )

            if (searchQuery.isEmpty()) {
                RecentCategoriesRow(
                    recentCategories = recentCategories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = onCategorySelected
                )
            }

            Text(
                text = "ALL CATEGORIES",
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

            CategoryGrid(
                categories = displayCategories,
                selectedCategory = selectedCategory,
                onCategorySelected = onCategorySelected,
                onAddNew = { showCreateCategory = true }
            )
        }
    }

    if (showCreateCategory) {
        CreateCategorySheet(
            onDismiss = { showCreateCategory = false },
            onCategoryCreated = { name, color, icon ->
                onCategoryCreated(name, color, icon) // ← bubble up to ViewModel
                showCreateCategory = false
            }
        )
    }
}

@Composable
private fun SheetHeader(onDismiss: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 4.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Select category",
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            letterSpacing = (-0.3).sp,
            color = TextPrimary
        )
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(SurfaceElevated)
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Close",
                tint = TextPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
@Preview(
    showBackground = true,
    backgroundColor = 0xFF141414
)
@Composable
private fun SheetHeaderPreview() {
    PennywiseTheme {
        SheetHeader(onDismiss = {})
    }
}