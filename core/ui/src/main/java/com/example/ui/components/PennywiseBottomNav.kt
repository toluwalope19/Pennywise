package com.example.ui.components



import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ui.theme.PennywiseTheme
import com.example.ui.theme.Surface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.utils.pressScale


// Routes — used to determine active tab
object PennywiseRoutes {
    const val DASHBOARD = "dashboard"
    const val TRANSACTIONS = "transactions"
    const val BUDGETS = "budgets"
    const val ANALYTICS = "analytics"
    const val SETTINGS = "settings"
}

@Composable
fun PennywiseBottomNav(
    activeRoute: String,
    onHomeClick: () -> Unit,
    onActivityClick: () -> Unit,
    onAddClick: () -> Unit,
    onBudgetsClick: () -> Unit,
    onAnalyticsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Surface)
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home
            BottomNavItem(
                activeIcon = Icons.Filled.Home,
                inactiveIcon = Icons.Outlined.Home,
                label = "Home",
                isActive = activeRoute == PennywiseRoutes.DASHBOARD,
                onClick = onHomeClick
            )

            // Activity
            BottomNavItem(
                activeIcon = Icons.Rounded.Schedule,
                inactiveIcon = Icons.Rounded.Schedule,
                label = "Activity",
                isActive = activeRoute == PennywiseRoutes.TRANSACTIONS,
                onClick = onActivityClick
            )

            // Centre FAB — gradient, no active state
            BottomNavFab(onClick = onAddClick)

            // Budgets
            BottomNavItem(
                activeIcon = Icons.Filled.PieChart,
                inactiveIcon = Icons.Outlined.PieChart,
                label = "Budgets",
                isActive = activeRoute == PennywiseRoutes.BUDGETS,
                onClick = onBudgetsClick
            )

            // Analytics
            BottomNavItem(
                activeIcon = Icons.Rounded.Analytics,
                inactiveIcon = Icons.Rounded.Analytics,
                label = "Analytics",
                isActive = activeRoute == PennywiseRoutes.ANALYTICS,
                onClick = onAnalyticsClick
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    activeIcon: ImageVector,
    inactiveIcon: ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(48.dp)
    ) {
        Icon(
            imageVector = if (isActive) activeIcon else inactiveIcon,
            contentDescription = label,
            tint = if (isActive) TextPrimary else TextSecondary,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF141414
)
@Composable
private fun PennywiseBottomNavPreview() {
    PennywiseTheme {
        PennywiseBottomNav(
            activeRoute = PennywiseRoutes.DASHBOARD,
            onHomeClick = {},
            onActivityClick = {},
            onAddClick = {},
            onBudgetsClick = {},
            onAnalyticsClick = {}
        )
    }
}