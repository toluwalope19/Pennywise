package com.example.transactions.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.example.transactions.list.TransactionListItem
import com.example.ui.theme.Accent

@Composable
fun TransactionsList(
    transactions: LazyPagingItems<TransactionListItem>,
    onTransactionClick: (Long) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 8.dp
        ),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(
            count = transactions.itemCount,
            key = transactions.itemKey { item ->
                when (item) {
                    is TransactionListItem.Header -> "header_${item.date}"
                    is TransactionListItem.Item -> "item_${item.transaction.id}"
                }
            }
        ) { index ->
            when (val item = transactions[index]) {
                is TransactionListItem.Header -> {
                    DateHeader(date = item.date)
                }
                is TransactionListItem.Item -> {
                    TransactionCard(
                        item = item,
                        onTransactionClick = onTransactionClick
                    )
                }
                null -> Unit
            }
        }

        // Loading more indicator at bottom
        if (transactions.loadState.append is LoadState.Loading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Accent,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}