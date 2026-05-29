package com.example.budgets.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Background
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.utils.pressScale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetsTopBar(
    selectedMonth: Int,
    selectedYear: Int,
    onMonthChanged: (Int) -> Unit,
    onAddClick: () -> Unit
) {
    TopAppBar(
        windowInsets = WindowInsets(top = 0),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Background
        ),
        title = {
            Text(
                text = "Budgets",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = TextPrimary,
                modifier = Modifier.padding(start = 4.dp)
            )
        },
        actions = {
            // Month navigator
            MonthNavigatorPill(
                selectedMonth = selectedMonth,
                selectedYear = selectedYear,
                onMonthChanged = onMonthChanged
            )

            Spacer(modifier = Modifier.size(8.dp))

            // New budget pill
            Box(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .pressScale()
                    .clip(RoundedCornerShape(50.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF8E78FF),
                                Color(0xFF6644FF)
                            )
                        )
                    )
            ) {
                Button(
                    onClick = onAddClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues(
                        start = 10.dp,
                        end = 14.dp,
                        top = 0.dp,
                        bottom = 0.dp
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        text = "New budget",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
            }
        }
    )
}

@Composable
private fun MonthNavigatorPill(
    selectedMonth: Int,
    selectedYear: Int,
    onMonthChanged: (Int) -> Unit
) {
    val monthName = java.time.Month.of(selectedMonth).name
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
