package com.example.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.PieChart
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ui.theme.Accent
import com.example.ui.theme.PennywiseTheme
import com.example.ui.theme.Surface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.google.api.Monitoring

@Composable
private fun DashboardBottomNav(
    onHomeClick: () -> Unit,
    onActivityClick: () -> Unit,
    onAddClick: () -> Unit,
    onBudgetsClick: () -> Unit,
    onAnalyticsClick: () -> Unit,
    activeRoute: String = "dashboard" // track which tab is active
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home — active
            NavItem(
                icon = Icons.Rounded.Home,
                label = "Home",
                isActive = true,
                onClick = onHomeClick
            )

            // Activity
            NavItem(
                icon = Icons.Rounded.Schedule,
                label = "Activity",
                isActive = false,
                onClick = onActivityClick
            )

            // Centre FAB
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF8E78FF),
                                Color(0xFF6644FF)
                            )
                        )
                    )
                    .clickable { onAddClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add transaction",
                    tint = TextPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }

            // Budgets
            NavItem(
                icon = Icons.Rounded.PieChart,
                label = "Budgets",
                isActive = false,
                onClick = onBudgetsClick
            )

            // Analytics
            NavItem(
                icon = Icons.Rounded.Analytics,
                label = "Analytics",
                isActive = false,
                onClick = onAnalyticsClick
            )
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    iconFilled: ImageVector = icon, // filled variant for active state
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(48.dp)
    ) {
        Icon(
            imageVector = if (isActive) iconFilled else icon,
            contentDescription = label,
            tint = if (isActive) TextPrimary else TextSecondary, // ← white when active
            modifier = Modifier.size(24.dp)
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF141414
)
@Composable
private fun DashboardBottomNavPreview() {
    PennywiseTheme {
        DashboardBottomNav(
            onHomeClick = {},
            onActivityClick = {},
            onAddClick = {},
            onBudgetsClick = {},
            onAnalyticsClick = {}
        )
    }
}