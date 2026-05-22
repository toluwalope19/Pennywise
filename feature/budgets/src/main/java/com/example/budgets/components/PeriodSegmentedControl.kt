package com.example.budgets.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.BudgetPeriod
import com.example.ui.theme.Border
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.PennywiseTheme
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextSecondary

@Composable
fun PeriodSegmentedControl(
    selectedPeriod: BudgetPeriod,
    onPeriodChanged: (BudgetPeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Section label
        Text(
            text = "PERIOD",
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            letterSpacing = 1.sp,
            color = TextSecondary
        )

        // Pill container
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50.dp))
                .background(SurfaceElevated)
                .border(
                    width = 1.dp,
                    color = Border,
                    shape = RoundedCornerShape(50.dp)
                )
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            BudgetPeriod.entries.forEach { period ->
                PeriodOption(
                    label = period.name
                        .lowercase()
                        .replaceFirstChar { it.uppercase() },
                    isSelected = selectedPeriod == period,
                    onClick = { onPeriodChanged(period) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun PeriodOption(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(50.dp))
            .then(
                if (isSelected) {
                    Modifier.background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF8E78FF),
                                Color(0xFF6644FF)
                            )
                        )
                    )
                } else {
                    Modifier.background(Color.Transparent)
                }
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontFamily = InterFontFamily,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            fontSize = 13.sp,
            color = if (isSelected) Color.White else TextSecondary
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0A0A0A
)
@Composable
private fun PeriodSegmentedControlPreview() {
    PennywiseTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Monthly selected
                PeriodSegmentedControl(
                    selectedPeriod = BudgetPeriod.MONTHLY,
                    onPeriodChanged = {}
                )
                // Weekly selected
                PeriodSegmentedControl(
                    selectedPeriod = BudgetPeriod.WEEKLY,
                    onPeriodChanged = {}
                )
                // Yearly selected
                PeriodSegmentedControl(
                    selectedPeriod = BudgetPeriod.YEARLY,
                    onPeriodChanged = {}
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
private fun PeriodOptionPreview() {
    PennywiseTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PeriodOption(
                label = "Monthly",
                isSelected = true,
                onClick = {},
                modifier = Modifier.weight(1f)
            )
            PeriodOption(
                label = "Weekly",
                isSelected = false,
                onClick = {},
                modifier = Modifier.weight(1f)
            )
        }
    }
}