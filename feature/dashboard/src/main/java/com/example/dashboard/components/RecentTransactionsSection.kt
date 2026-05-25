package com.example.dashboard.components


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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.common.utils.CurrencyFormatter
import com.example.domain.model.Category
import com.example.domain.model.Transaction
import com.example.domain.model.TransactionType
import com.example.ui.components.CategoryDisplay
import com.example.ui.components.CategoryIcon
import com.example.ui.components.CategoryIconShape
import com.example.ui.components.CategoryType
import com.example.ui.components.TransactionRow
import com.example.ui.components.toDisplay
import com.example.ui.theme.Expense
import com.example.ui.theme.Income
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.PennywiseTheme
import com.example.ui.theme.Surface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.time.LocalDate


// ── Recent transactions section ────────────────────────────────────────────

@Composable
fun RecentTransactionsSection(
    transactions: List<Transaction>,
    onSeeAllClick: () -> Unit,
    categoryMap: Map<Long, Category>,
    onTransactionClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Section header
        RecentTransactionsHeader(onSeeAllClick = onSeeAllClick)

        // Transactions card
        if (transactions.isEmpty()) {
            RecentTransactionsEmpty()
        } else {
            RecentTransactionsCard(
                transactions = transactions,
                onTransactionClick = onTransactionClick,
                categoryMap = categoryMap
            )
        }
    }
}

// ── Section header ─────────────────────────────────────────────────────────

@Composable
private fun RecentTransactionsHeader(
    onSeeAllClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Recent Expenses",
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = TextPrimary
        )
        Text(
            text = "View all",
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            color = TextSecondary,
            modifier = Modifier.clickable { onSeeAllClick() }
        )
    }
}

// ── Transactions card ──────────────────────────────────────────────────────

@Composable
private fun RecentTransactionsCard(
    transactions: List<Transaction>,
    categoryMap: Map<Long, Category>,
    onTransactionClick: (Long) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Surface)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.04f),
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Column {
            transactions.forEachIndexed { index, transaction ->
                TransactionRow(
                    transaction = transaction,
                    categoryDisplay = categoryMap[transaction.categoryId]
                        ?.toDisplay()
                        ?: CategoryDisplay(
                            name = "Other",
                            icon = Icons.Rounded.MoreHoriz,
                            color = Color(0xFF8C8C8C)
                        ),
                    onClick = { onTransactionClick(transaction.id) }
                )
                // Divider between rows — not after last
                if (index < transactions.lastIndex) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .padding(horizontal = 16.dp)
                            .background(Color.White.copy(alpha = 0.05f))
                    )
                }
            }
        }
    }
}


// ── Empty state ────────────────────────────────────────────────────────────

@Composable
private fun RecentTransactionsEmpty() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Surface)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.04f),
                shape = RoundedCornerShape(20.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No transactions this month",
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = TextSecondary.copy(alpha = 0.6f)
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0A0A0A
)
@Composable
private fun RecentTransactionsSectionPreview() {
    PennywiseTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            RecentTransactionsSection(
                transactions = emptyList(),
                onSeeAllClick = {},
                onTransactionClick = {},
                categoryMap = HashMap()
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0A0A0A
)
@Composable
private fun RecentTransactionsSectionPreviewTwo() {
    PennywiseTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            RecentTransactionsSection(
                transactions = listOf(
                    Transaction(
                        id = 1L,
                        amount = 44.80,
                        type = TransactionType.EXPENSE,
                        categoryId = 1L,
                        note = "Tartine Bakery",
                        date = LocalDate.now()
                    ),
                    Transaction(
                        id = 2L,
                        amount = 59.00,
                        type = TransactionType.EXPENSE,
                        categoryId = 3L,
                        note = "FitLab Membership",
                        date = LocalDate.now()
                    ),
                    Transaction(
                        id = 3L,
                        amount = 18.42,
                        type = TransactionType.EXPENSE,
                        categoryId = 4L,
                        note = "Uber",
                        date = LocalDate.now().minusDays(1)
                    ),
                    Transaction(
                        id = 4L,
                        amount = 129.50,
                        type = TransactionType.EXPENSE,
                        categoryId = 2L,
                        note = "Zara",
                        date = LocalDate.now().minusDays(1)
                    ),
                    Transaction(
                        id = 5L,
                        amount = 2400.00,
                        type = TransactionType.INCOME,
                        categoryId = 8L,
                        note = "Acme Co · Salary",
                        date = LocalDate.now()
                    )
                ),
                onSeeAllClick = {},
                onTransactionClick = {},
                categoryMap = HashMap()
            )
        }
    }
}