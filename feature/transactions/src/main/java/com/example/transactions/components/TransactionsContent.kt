package com.example.transactions.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.transactions.TransactionListItem
import com.example.transactions.TransactionsUiEvent
import com.example.transactions.TransactionsUiState
import com.example.ui.theme.Accent
import com.example.ui.theme.Background

@Composable
fun TransactionsContent(
    state: TransactionsUiState,
    transactions: LazyPagingItems<TransactionListItem>,
    onEvent: (TransactionsUiEvent) -> Unit
) {
    Scaffold(
        containerColor = Background,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TransactionsTopBar(
                selectedMonth = state.selectedMonth,
                selectedYear = state.selectedYear,
                onBackClick = { onEvent(TransactionsUiEvent.OnBackClick) },
                onMonthChanged = { direction ->
                    onEvent(TransactionsUiEvent.OnMonthChanged(direction))
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filter chips row
            FilterChipsRow(
                selectedFilter = state.selectedFilter,
                selectedMonth = state.selectedMonth,
                selectedYear = state.selectedYear,
                onFilterChanged = { filter ->
                    onEvent(TransactionsUiEvent.OnFilterChanged(filter))
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Paged list
            when {
                transactions.loadState.refresh is LoadState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Accent)
                    }
                }
                transactions.itemCount == 0 -> {
                    TransactionsEmptyState()
                }
                else -> {
                    TransactionsList(
                        transactions = transactions,
                        onTransactionClick = { id ->
                            onEvent(TransactionsUiEvent.OnTransactionClick(id))
                        }
                    )
                }
            }
        }
    }
}