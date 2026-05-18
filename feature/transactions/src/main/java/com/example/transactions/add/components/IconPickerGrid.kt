package com.example.transactions.add.components

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
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Cake
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.ContentCut
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocalBar
import androidx.compose.material.icons.rounded.LocalFlorist
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Accent
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.PennywiseTheme
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextSecondary

private val categoryIcons = listOf(
    Icons.Rounded.Pets,
    Icons.Rounded.Favorite,
    Icons.Rounded.CardGiftcard,
    Icons.Rounded.Cake,
    Icons.Rounded.SportsEsports,
    Icons.Rounded.Checkroom,
    Icons.Rounded.Eco,
    Icons.Rounded.ContentCut,
    Icons.Rounded.School,
    Icons.Rounded.Work,
    Icons.Rounded.Smartphone,
    Icons.Rounded.Build,
    Icons.Rounded.Home,
    Icons.Rounded.LocalFlorist,
    Icons.Rounded.Coffee,
    Icons.Rounded.LocalBar,
    Icons.Rounded.AttachMoney,
    Icons.Rounded.MoreHoriz
)

// ── Icon grid ──────────────────────────────────────────────────────────────

@Composable
fun IconPickerGrid(
    selectedIcon: ImageVector,
    onIconSelected: (ImageVector) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Section label
        Text(
            text = "ICON",
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            letterSpacing = 1.sp,
            color = TextSecondary
        )

        // 3 rows of 6 icons
        categoryIcons.chunked(6).forEach { rowIcons ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowIcons.forEach { icon ->
                    IconCell(
                        icon = icon,
                        isSelected = selectedIcon == icon,
                        onClick = { onIconSelected(icon) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// ── Icon cell ──────────────────────────────────────────────────────────────

@Composable
private fun IconCell(
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceElevated)
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 1.5.dp,
                        color = Accent,
                        shape = RoundedCornerShape(12.dp)
                    )
                } else Modifier
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) Accent else TextSecondary,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0A0A0A
)
@Composable
private fun IconCellPreview() {
    PennywiseTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Selected
            IconCell(
                icon = Icons.Rounded.Pets,
                isSelected = true,
                onClick = {},
                modifier = Modifier.size(48.dp)
            )
            // Unselected
            IconCell(
                icon = Icons.Rounded.Favorite,
                isSelected = false,
                onClick = {},
                modifier = Modifier.size(48.dp)
            )
            // Unselected
            IconCell(
                icon = Icons.Rounded.Coffee,
                isSelected = false,
                onClick = {},
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0A0A0A
)
@Composable
private fun IconPickerGridPreview() {
    PennywiseTheme {
        IconPickerGrid(
            selectedIcon = Icons.Rounded.Pets,
            onIconSelected = {}
        )
    }
}