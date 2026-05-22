package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CategoryIcon
import com.example.ui.components.CategoryIconShape
import com.example.ui.components.CategoryType
import com.example.ui.theme.Accent
import com.example.ui.theme.Border
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.PennywiseTheme
import com.example.ui.theme.Surface
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

// ── Bottom buttons ─────────────────────────────────────────────────────────

@Composable
fun CreateCategoryButtons(
    name: String,
    onCancel: () -> Unit,
    onCreate: () -> Unit
) {
    val isValid = name.isNotBlank()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp, top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Cancel button
        Box(
            modifier = Modifier
                .weight(1f)
                .height(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceElevated)
                .border(
                    width = 1.dp,
                    color = Border,
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable { onCancel() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Cancel",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = TextPrimary
            )
        }

        // Create category button — purple gradient
        Box(
            modifier = Modifier
                .weight(2f)
                .height(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = if (isValid) {
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF8E78FF),
                                Color(0xFF6644FF)
                            )
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF8E78FF).copy(alpha = 0.4f),
                                Color(0xFF6644FF).copy(alpha = 0.4f)
                            )
                        )
                    }
                )
                .clickable(enabled = isValid) { onCreate() },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Create category",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (isValid) Color.White else Color.White.copy(alpha = 0.5f)
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
private fun CreateCategoryButtonsDisabledPreview() {
    PennywiseTheme {
        CreateCategoryButtons(
            name = "",
            onCancel = {},
            onCreate = {}
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0A0A0A
)
@Composable
private fun CreateCategoryButtonsEnabledPreview() {
    PennywiseTheme {
        CreateCategoryButtons(
            name = "Pets",
            onCancel = {},
            onCreate = {}
        )
    }
}
