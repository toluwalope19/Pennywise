package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.PennywiseTheme
import com.example.ui.theme.TextSecondary

// The 12 colors from the design
private val categoryColors = listOf(
    Color(0xFFFF8A3D), // orange
    Color(0xFFE040FB), // purple
    Color(0xFF00E5A0), // teal
    Color(0xFF4FC3F7), // light blue
    Color(0xFF7B61FF), // accent purple
    Color(0xFFFFD25A), // yellow
    Color(0xFFFF7AC1), // pink
    Color(0xFFFF4D6A), // red
    Color(0xFF8BC34A), // green
    Color(0xFF26C6DA), // cyan
    Color(0xFFBA68C8), // lavender
    Color(0xFF9E9E9E)  // gray
)

@Composable
fun ColorSwatchGrid(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Section label
        Text(
            text = "COLOR",
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            letterSpacing = 1.sp,
            color = TextSecondary
        )

        // 2 rows of 6 swatches each
        categoryColors.chunked(6).forEach { rowColors ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowColors.forEach { color ->
                    ColorSwatch(
                        color = color,
                        isSelected = selectedColor == color,
                        onClick = { onColorSelected(color) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorSwatch(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f) // always square
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = Color.White.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(12.dp)
                    )
                } else Modifier
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = "Selected",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0A0A0A
)
@Composable
private fun ColorSwatchGridPreview() {
    PennywiseTheme {
        ColorSwatchGrid(
            selectedColor = Color(0xFFFF8A3D),
            onColorSelected = {}
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0A0A0A
)
@Composable
private fun ColorSwatchPreview() {
    PennywiseTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ColorSwatch(
                color = Color(0xFFFF8A3D),
                isSelected = true,
                onClick = {},
                modifier = Modifier.size(52.dp)
            )
            ColorSwatch(
                color = Color(0xFF7B61FF),
                isSelected = false,
                onClick = {},
                modifier = Modifier.size(52.dp)
            )
        }
    }
}