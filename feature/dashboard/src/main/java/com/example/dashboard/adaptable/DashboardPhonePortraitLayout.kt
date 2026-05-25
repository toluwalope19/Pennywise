package com.example.dashboard.adaptable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dashboard.DashboardUiEvent
import com.example.dashboard.DashboardUiState
import com.example.dashboard.components.BalanceHeroSection
import com.example.dashboard.components.DonutCard
import com.example.dashboard.components.RecentTransactionsSection

@Composable
fun DashboardPhonePortraitLayout(
    state: DashboardUiState,
    onEvent: (DashboardUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 12.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            BalanceHeroSection(
                totalBalance = state.totalBalance,
                totalIncome = state.totalIncome,
                totalExpense = state.totalExpense
            )
        }
        item {
            DonutCard(
                totalExpense = state.totalExpense,
                categorySpending = state.spendingByCategory
            )
        }
        item {
            RecentTransactionsSection(
                transactions = state.recentTransactions,
                categoryMap = state.categoryMap,
                onSeeAllClick = {
                    onEvent(DashboardUiEvent.OnSeeAllTransactionsClick)
                },
                onTransactionClick = { id ->
                    onEvent(DashboardUiEvent.OnTransactionClick(id))
                }
            )
        }
    }
}