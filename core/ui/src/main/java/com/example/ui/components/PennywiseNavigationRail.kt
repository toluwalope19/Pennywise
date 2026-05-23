package com.example.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Accent
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.PennywiseTheme
import com.example.ui.theme.Surface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PennywiseNavigationRail(
    activeRoute: String,
    userName: String = "Alex Park",
    onHomeClick: () -> Unit,
    onActivityClick: () -> Unit,
    onAddClick: () -> Unit,
    onBudgetsClick: () -> Unit,
    onAnalyticsClick: () -> Unit,
    onAvatarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val initials = userName
        .split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")

    Box(
        modifier = modifier
            .width(80.dp)
            .fillMaxHeight()
            .background(Surface)
            // Subtle right border — separates rail from content
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(0.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // ── "P" Logo ──────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp)) // ← was CircleShape
                    .background(Accent),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "P",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Home ──────────────────────────────────────────────────────
            RailItem(
                activeIcon = Icons.Filled.Home,
                inactiveIcon = Icons.Outlined.Home,
                label = "Home",
                isActive = activeRoute == PennywiseRoutes.DASHBOARD,
                onClick = onHomeClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── Activity ──────────────────────────────────────────────────
            RailItem(
                activeIcon = Icons.Rounded.Schedule,
                inactiveIcon = Icons.Rounded.Schedule,
                label = "Activity",
                isActive = activeRoute == PennywiseRoutes.TRANSACTIONS,
                onClick = onActivityClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── FAB ───────────────────────────────────────────────────────
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
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Budgets ───────────────────────────────────────────────────
            RailItem(
                activeIcon = Icons.Filled.PieChart,
                inactiveIcon = Icons.Outlined.PieChart,
                label = "Budgets",
                isActive = activeRoute == PennywiseRoutes.BUDGETS,
                onClick = onBudgetsClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── Analytics ─────────────────────────────────────────────────
            RailItem(
                activeIcon = Icons.Rounded.Analytics,
                inactiveIcon = Icons.Outlined.Analytics,
                label = "Analytics",
                isActive = activeRoute == PennywiseRoutes.ANALYTICS,
                onClick = onAnalyticsClick
            )

            // ── Push avatar to bottom ──────────────────────────────────────
            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Accent)
                    .clickable { onAvatarClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }
    }
}


@Composable
private fun RailItem(
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
    backgroundColor = 0xFF0A0A0A,
    heightDp = 844,
    widthDp = 80
)
@Composable
private fun PennywiseNavigationRailPreview() {
    PennywiseTheme {
        PennywiseNavigationRail(
            activeRoute = PennywiseRoutes.DASHBOARD,
            userName = "Alex Park",
            onHomeClick = {},
            onActivityClick = {},
            onAddClick = {},
            onBudgetsClick = {},
            onAnalyticsClick = {},
            onAvatarClick = {}
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0A0A0A,
    heightDp = 844,
    widthDp = 80
)
@Composable
private fun PennywiseNavigationRailActivityActivePreview() {
    PennywiseTheme {
        PennywiseNavigationRail(
            activeRoute = PennywiseRoutes.TRANSACTIONS,
            userName = "Alex Park",
            onHomeClick = {},
            onActivityClick = {},
            onAddClick = {},
            onBudgetsClick = {},
            onAnalyticsClick = {},
            onAvatarClick = {}
        )
    }
}