package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class CategoryIconShape { CIRCLE, ROUNDED_SQUARE }

@Composable
fun CategoryIcon(
    categoryType: CategoryType,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    shape: CategoryIconShape = CategoryIconShape.ROUNDED_SQUARE
) {
    val clipShape = when (shape) {
        CategoryIconShape.CIRCLE -> CircleShape
        CategoryIconShape.ROUNDED_SQUARE -> RoundedCornerShape(12.dp)
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(clipShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        categoryType.gradientStart,
                        categoryType.gradientEnd
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = categoryType.icon,
            contentDescription = categoryType.displayName,
            tint = Color.White,
            modifier = Modifier.size(size * 0.5f)
        )
    }
}

// Settings icon — custom gradient per row, any ImageVector
@Composable
fun SettingsIcon(
    icon: ImageVector,
    gradientStart: Color,
    gradientEnd: Color,
    modifier: Modifier = Modifier,
    size: Dp = 36.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(10.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(gradientStart, gradientEnd)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(size * 0.55f)
        )
    }
}