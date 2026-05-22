package com.example.budgets.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.ChevronRight
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
import com.example.domain.model.Category
import com.example.ui.components.CategoryDisplay
import com.example.ui.components.toDisplay
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.SurfaceElevated

// ── Category field ─────────────────────────────────────────────────────────

@Composable
fun BudgetCategoryField(
    selectedCategory: Category?,
    onClick: () -> Unit
) {
    val display = selectedCategory?.toDisplay()
        ?: CategoryDisplay(
            name = "Select category",
            icon = Icons.Rounded.Category,
            color = SurfaceElevated
        )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(com.example.ui.theme.Surface)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.04f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Category icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(display.color),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = display.icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        // Label + value
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "CATEGORY",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                letterSpacing = 0.8.sp,
                color = com.example.ui.theme.TextSecondary
            )
            Text(
                text = display.name,
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = if (selectedCategory == null)
                    com.example.ui.theme.TextSecondary
                else
                    com.example.ui.theme.TextPrimary,
                modifier = Modifier.offset(y = (-3).dp)
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = null,
            tint = com.example.ui.theme.TextSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}