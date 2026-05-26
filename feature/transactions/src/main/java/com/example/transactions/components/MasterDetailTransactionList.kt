package com.example.transactions.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.example.domain.model.Category
import com.example.transactions.list.TransactionListItem
import com.example.transactions.list.TransactionsUiEvent
import com.example.transactions.list.TransactionsUiState
import com.example.ui.components.CategoryDisplay
import com.example.ui.components.toDisplay
import com.example.ui.theme.Accent
import com.example.ui.theme.Background
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.TextSecondary

@Composable
fun MasterDetailTransactionList(
    transactions: LazyPagingItems<TransactionListItem>,
    categoryMap: Map<Long, Category>,
    selectedTransactionId: Long?,
    onTransactionSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(14.dp)
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
                    Text(
                        text = item.date.toString().uppercase(),
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 1.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(
                            start = 4.dp,
                            bottom = 6.dp
                        )
                    )
                }
                is TransactionListItem.Item -> {
                    val transaction = item.transaction
                    val isSelected = transaction.id == selectedTransactionId
                    val display = categoryMap[transaction.categoryId]
                        ?.toDisplay()
                        ?: CategoryDisplay(
                            name = "Other",
                            icon = Icons.Rounded.MoreHoriz,
                            color = Color(0xFF8C8C8C)
                        )

                    MasterDetailTransactionRow(
                        transaction = transaction,
                        categoryDisplay = display,
                        isSelected = isSelected,
                        onClick = { onTransactionSelected(transaction.id) }
                    )
                }
                null -> Unit
            }
        }
    }
}