package com.example.analytics.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.analytics.MonthlyData

@Composable
fun BarChart(
    monthlyData: List<MonthlyData>,
    modifier: Modifier = Modifier
) {
    if (monthlyData.isEmpty()) return

    val maxValue = monthlyData.maxOf { maxOf(it.income, it.expense) }
        .takeIf { it > 0 } ?: 1.0

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        monthlyData.forEach { data ->
            BarColumn(
                data = data,
                maxValue = maxValue,
                modifier = Modifier.weight(1f)
            )
        }
    }
}