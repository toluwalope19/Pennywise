package com.example.transactions.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.transactions.list.TransactionListItem
import com.example.ui.components.CategoryType
import com.example.ui.components.TransactionRow
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.Surface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TransactionCard(
    item: TransactionListItem.Item,
    onTransactionClick: (Long) -> Unit
) {
    val transaction = item.transaction

    val categoryType = when (transaction.categoryId) {
        1L -> CategoryType.FOOD
        2L -> CategoryType.SHOPPING
        3L -> CategoryType.HEALTH
        4L -> CategoryType.TRANSPORT
        5L -> CategoryType.EDUCATION
        6L -> CategoryType.UTILITIES
        7L -> CategoryType.TRAVEL
        8L -> CategoryType.INCOME
        else -> CategoryType.OTHER
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Surface)
    ) {
        TransactionRow(
            transaction = transaction,
            categoryType = categoryType,
            onClick = { onTransactionClick(transaction.id) }
        )
    }
}

@Composable
fun TransactionsEmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "No transactions",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = TextPrimary
            )
            Text(
                text = "Tap + to add your first transaction",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = TextSecondary
            )
        }
    }
}