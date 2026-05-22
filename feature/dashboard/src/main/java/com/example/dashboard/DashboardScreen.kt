package com.example.dashboard

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
import com.example.dashboard.components.BalanceHeroSection
import com.example.dashboard.components.DashboardTopBar
import com.example.dashboard.components.DonutCard
import com.example.dashboard.components.RecentTransactionsSection
import com.example.ui.components.PennywiseBottomNav
import com.example.ui.components.PennywiseRoutes
import com.example.ui.theme.Accent
import com.example.ui.theme.Background


@Composable
fun DashboardScreen(
    onNavigateToTransactions: () -> Unit,
    onNavigateToTransaction: (Long) -> Unit,
    onNavigateToAddTransaction: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Collect one-shot effects
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                DashboardUiEffect.NavigateToTransactions -> onNavigateToTransactions()
                is DashboardUiEffect.NavigateToTransaction -> onNavigateToTransaction(effect.id)
                is DashboardUiEffect.NavigateToAddTransaction -> onNavigateToAddTransaction(effect.type)
                DashboardUiEffect.NavigateToSettings -> onNavigateToSettings()
            }
        }
    }

    DashboardContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun DashboardContent(
    state: DashboardUiState,
    onEvent: (DashboardUiEvent) -> Unit
) {
    Scaffold(
        containerColor = Background,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            DashboardTopBar(
                userName = "Alex Park", // comes from DataStore later
                selectedMonth = state.selectedMonth,
                selectedYear = state.selectedYear,
                onMonthChanged = { direction ->
                    onEvent(DashboardUiEvent.OnMonthChanged(direction))
                },
                onAvatarClick = {
                    onEvent(DashboardUiEvent.OnSettingsClick)
                }
            )
        }
    )  { paddingValues ->

        if (state.isLoading && state.recentTransactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Accent)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues), // ← this is the key line
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Components slot in here one by one
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
                    categorySpending = state.spendingByCategory,
                )
            }
            item {
                RecentTransactionsSection(
                    transactions = state.recentTransactions,
                    onSeeAllClick = {
                        onEvent(DashboardUiEvent.OnSeeAllTransactionsClick)
                    },
                    categoryMap = state.categoryMap,
                    onTransactionClick = { id ->
                        onEvent(DashboardUiEvent.OnTransactionClick(id))
                    }
                )
            }
        }
    }
}

