package com.example.transactions.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.common.utils.CurrencyFormatter
import com.example.domain.model.Transaction
import com.example.domain.model.TransactionType
import com.example.transactions.components.FilterChipsRow
import com.example.transactions.components.TransactionsContent
import com.example.ui.PennywiseWindowLayout
import com.example.ui.components.CategoryDisplay
import com.example.ui.theme.Income
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MasterDetailTransactionRow(
    transaction: Transaction,
    categoryDisplay: CategoryDisplay,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val amountText = if (transaction.type == TransactionType.INCOME) {
        "+${CurrencyFormatter.formatWithSymbol(transaction.amount)}"
    } else {
        "-${CurrencyFormatter.formatWithSymbol(transaction.amount)}"
    }
    val amountColor = if (transaction.type == TransactionType.INCOME) Income else TextPrimary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isSelected) Color(0xFF7B61FF).copy(alpha = 0.08f)
                else Color.Transparent
            )
            .clickable { onClick() }
            .padding(
                horizontal = if (isSelected) 8.dp else 0.dp,
                vertical = 10.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(categoryDisplay.color),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = categoryDisplay.icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.note ?: categoryDisplay.name,
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = categoryDisplay.name,
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 1.dp)
            )
        }

        Text(
            text = amountText,
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = amountColor
        )
    }
}