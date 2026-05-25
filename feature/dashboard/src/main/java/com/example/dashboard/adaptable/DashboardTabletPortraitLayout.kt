package com.example.dashboard.adaptable


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dashboard.DashboardUiEvent
import com.example.dashboard.DashboardUiState
import com.example.dashboard.components.BalanceHeroSection
import com.example.dashboard.components.DashboardTopBar
import com.example.dashboard.components.DonutCard
import com.example.dashboard.components.RecentTransactionsSection
import com.example.ui.PennywiseWindowLayout
import com.example.ui.components.PennywiseBottomNav
import com.example.ui.components.PennywiseRoutes
import com.example.ui.theme.Accent
import com.example.ui.theme.Background

@Composable
fun DashboardTabletPortraitLayout(
    state: DashboardUiState,
    onEvent: (DashboardUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            horizontal = 24.dp, // ← wider padding on tablet
            vertical = 20.dp
        ),
        verticalArrangement = Arrangement.spacedBy(20.dp)
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