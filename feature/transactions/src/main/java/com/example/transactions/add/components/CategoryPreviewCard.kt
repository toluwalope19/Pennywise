package com.example.transactions.add.components

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
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CategoryType
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import androidx.core.graphics.toColorInt
import com.example.ui.theme.PennywiseTheme

@Composable
fun CategoryPreviewCard(
    name: String,
    selectedColor: Color,       // ← direct Color, not hex string
    selectedIcon: ImageVector   // ← direct ImageVector, not icon name string
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 20.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceElevated)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.04f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(selectedColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = selectedIcon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = "PREVIEW",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                letterSpacing = 0.8.sp,
                color = TextSecondary
            )
            Text(
                text = name.ifBlank { "Category name" },
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp,
                color = if (name.isBlank()) TextSecondary else TextPrimary
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF141414
)
@Composable
private fun CategoryPreviewCardEmptyPreview() {
    PennywiseTheme {
        CategoryPreviewCard(
            name = "",
            selectedColor = Color(0xFFFF8A3D),
            selectedIcon = Icons.Rounded.Restaurant
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF141414
)
@Composable
private fun CategoryPreviewCardFilledPreview() {
    PennywiseTheme {
        CategoryPreviewCard(
            name = "Pets",
            selectedColor = Color(0xFFFF8A3D),
            selectedIcon = Icons.Rounded.Pets
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF141414
)
@Composable
private fun CategoryPreviewCardHealthPreview() {
    PennywiseTheme {
        CategoryPreviewCard(
            name = "Health & Fitness",
            selectedColor = Color(0xFF5AE9C8),
            selectedIcon = Icons.Rounded.FitnessCenter
        )
    }
}