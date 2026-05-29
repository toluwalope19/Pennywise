package com.example.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.common.utils.CurrencyFormatter
import com.example.domain.model.Transaction
import com.example.domain.model.TransactionType
import com.example.ui.theme.Income
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.utils.pressScale
import java.time.LocalDate

@Composable
fun TransactionRow(
    transaction: Transaction,
    categoryDisplay: CategoryDisplay,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateLabel = when (transaction.date) {
        LocalDate.now() -> "Today"
        LocalDate.now().minusDays(1) -> "Yesterday"
        else -> transaction.date.dayOfWeek.name
            .lowercase()
            .replaceFirstChar { it.uppercase() }
            .take(3)
    }

    val amountText = if (transaction.type == TransactionType.INCOME) {
        "+${CurrencyFormatter.formatWithSymbol(transaction.amount)}"
    } else {
        "-${CurrencyFormatter.formatWithSymbol(transaction.amount)}"
    }

    val amountColor = if (transaction.type == TransactionType.INCOME) {
        Income
    } else {
        TextPrimary
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .pressScale(scaleDown = 0.98f)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(categoryDisplay.color),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = categoryDisplay.icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = transaction.note ?: categoryDisplay.name,
                fontFamily = InterFontFamily,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                fontSize = 15.sp,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "$dateLabel · ${categoryDisplay.name}",
                fontFamily = InterFontFamily,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
                fontSize = 13.sp,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 1.dp)
            )
        }

        Text(
            text = amountText,
            fontFamily = InterFontFamily,
            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
            fontSize = 15.sp,
            color = amountColor
        )
    }
}