package com.example.transactions.add.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.TransactionType
import com.example.ui.theme.Accent
import com.example.ui.theme.Background
import com.example.ui.theme.Border
import com.example.ui.theme.Income
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.PennywiseTheme
import com.example.ui.theme.Surface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TypeToggle(
    selectedType: TransactionType,
    onTypeChanged: (TransactionType) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(Surface)
            .border(
                width = 1.dp,
                color = Border,
                shape = RoundedCornerShape(50.dp)
            )
            .padding(4.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Expense button
            TypeToggleButton(
                label = "Expense",
                icon = Icons.Rounded.ArrowDownward,
                isActive = selectedType == TransactionType.EXPENSE,
                activeGradient = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFF6580),
                        Color(0xFFFF4D6A)
                    )
                ),
                glowColor = Color(0xFFFF4D6A),
                modifier = Modifier.weight(1f),
                onClick = { onTypeChanged(TransactionType.EXPENSE) }
            )

            // Income button
            TypeToggleButton(
                label = "Income",
                icon = Icons.Rounded.ArrowUpward,
                isActive = selectedType == TransactionType.INCOME,
                activeGradient = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF00E5A0),
                        Color(0xFF0BA67B)
                    )
                ),
                glowColor = Color(0xFF00E5A0),
                modifier = Modifier.weight(1f),
                onClick = { onTypeChanged(TransactionType.INCOME) }
            )
        }
    }
}

@Composable
private fun TypeToggleButton(
    label: String,
    icon: ImageVector,
    isActive: Boolean,
    activeGradient: Brush,
    glowColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(50.dp))
            .then(
                if (isActive) {
                    Modifier
                        .background(activeGradient)
                        .shadow(
                            elevation = 0.dp,
                            shape = RoundedCornerShape(50.dp),
                            ambientColor = glowColor.copy(alpha = 0.35f),
                            spotColor = glowColor.copy(alpha = 0.35f)
                        )
                } else {
                    Modifier.background(Color.Transparent)
                }
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isActive) TextPrimary else TextSecondary,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = if (isActive) TextPrimary else TextSecondary
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0A0A0A
)
@Composable
private fun TypeTogglePreview() {
    PennywiseTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            TypeToggle(
                selectedType = TransactionType.EXPENSE,
                onTypeChanged = {}
            )
        }
    }
}