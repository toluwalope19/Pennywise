package com.example.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.ui.theme.Accent
import com.example.ui.theme.Background
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.PennywiseTheme
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.time.Month

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTopBar(
    userName: String,
    selectedMonth: Int,
    selectedYear: Int,
    onMonthChanged: (Int) -> Unit,
    onAvatarClick: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Background
        ),
        windowInsets = WindowInsets(0),
        title = {
            Column(
                modifier = Modifier
            ) {
                Text(
                    text = "WELCOME BACK",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    letterSpacing = 0.8.sp,
                    color = TextSecondary
                )
                Text(
                    text = userName,
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    letterSpacing = (-0.3).sp,
                    color = TextPrimary
                )
            }
        },
        actions = {
            MonthNavigatorPill(
                selectedMonth = selectedMonth,
                selectedYear = selectedYear,
                onMonthChanged = onMonthChanged
            )
            Spacer(modifier = Modifier.width(8.dp))
            UserAvatar(
                userName = userName,
                onClick = onAvatarClick
            )
            Spacer(modifier = Modifier.width(16.dp))
        }
    )
}

@Composable
fun MonthNavigatorPill(
    selectedMonth: Int,
    selectedYear: Int,
    onMonthChanged: (Int) -> Unit
) {
    val monthName = Month.of(selectedMonth).name
        .lowercase()
        .replaceFirstChar { it.uppercase() }

    Surface(
        shape = RoundedCornerShape(50.dp),
        color = SurfaceElevated
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(36.dp)
        ) {
            IconButton(
                onClick = { onMonthChanged(-1) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.ChevronLeft,
                    contentDescription = "Previous month",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = monthName,
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = TextPrimary
            )
            IconButton(
                onClick = { onMonthChanged(1) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = "Next month",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun UserAvatar(
    userName: String,
    onClick: () -> Unit
) {
    val initials = userName
        .split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Accent)
            .clickable { onClick() },
        contentAlignment = Center
    ) {
        Text(
            text = initials,
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = TextPrimary
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0A0A0A
)
@Composable
private fun DashboardTopBarPreview() {
    PennywiseTheme {
        DashboardTopBar(
            userName = "Alex Park",
            selectedMonth = 5,
            selectedYear = 2026,
            onMonthChanged = {},
            onAvatarClick = {}
        )
    }
}