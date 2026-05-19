package com.example.transactions.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Category
import com.example.transactions.list.TransactionListItem
import com.example.ui.components.CategoryDisplay
import com.example.ui.components.CategoryType
import com.example.ui.components.TransactionRow
import com.example.ui.components.toDisplay
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.Surface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TransactionCard(
    item: TransactionListItem.Item,
    categoryMap: Map<Long, Category>,
    onTransactionClick: (Long) -> Unit
) {
    val transaction = item.transaction

    val categoryDisplay = categoryMap[transaction.categoryId]
        ?.toDisplay()
        ?: CategoryDisplay(
            name = "Other",
            icon = Icons.Rounded.MoreHoriz,
            color = Color(0xFF8C8C8C)
        )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Surface)
    ) {
        TransactionRow(
            transaction = transaction,
            categoryDisplay = categoryDisplay,
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