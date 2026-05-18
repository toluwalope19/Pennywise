package com.example.transactions.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.transactions.list.TransactionFilter
import com.example.ui.theme.TextSecondary
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun FilterChipsRow(
    selectedFilter: TransactionFilter,
    selectedMonth: Int,
    selectedYear: Int,
    onFilterChanged: (TransactionFilter) -> Unit
) {
    val monthName = Month.of(selectedMonth)
        .getDisplayName(TextStyle.FULL, Locale.getDefault())

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(TransactionFilter.entries) { filter ->
            PennywiseFilterChip(
                label = filter.displayName(),
                isSelected = selectedFilter == filter,
                onClick = { onFilterChanged(filter) }
            )
        }
        item {
            PennywiseFilterChip(
                label = "$monthName $selectedYear",
                isSelected = false,
                onClick = { },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.CalendarMonth,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            )
        }
    }
}