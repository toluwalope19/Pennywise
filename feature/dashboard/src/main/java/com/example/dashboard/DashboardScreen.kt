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
import com.example.dashboard.adaptable.DashboardFoldableLayout
import com.example.dashboard.adaptable.DashboardPhoneLandscapeLayout
import com.example.dashboard.adaptable.DashboardPhonePortraitLayout
import com.example.dashboard.adaptable.DashboardTabletLandscapeLayout
import com.example.dashboard.adaptable.DashboardTabletPortraitLayout
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
fun DashboardScreen(
    onNavigateToTransactions: () -> Unit,
    onNavigateToTransaction: (Long) -> Unit,
    onNavigateToAddTransaction: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    windowLayout: PennywiseWindowLayout = PennywiseWindowLayout.PhonePortrait,
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
        windowLayout = windowLayout,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun DashboardContent(
    state: DashboardUiState,
    windowLayout: PennywiseWindowLayout,
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

        when (windowLayout) {
            is PennywiseWindowLayout.PhonePortrait ->
                DashboardPhonePortraitLayout(
                    state = state,
                    onEvent = onEvent,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            is PennywiseWindowLayout.PhoneLandscape ->
                DashboardPhoneLandscapeLayout(
                    state = state,
                    onEvent = onEvent,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            is PennywiseWindowLayout.TabletPortrait ->
                DashboardTabletPortraitLayout(
                    state = state,
                    onEvent = onEvent,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            is PennywiseWindowLayout.TabletLandscape ->
                DashboardTabletLandscapeLayout(
                    state = state,
                    onEvent = onEvent,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            is PennywiseWindowLayout.Foldable ->
                DashboardFoldableLayout(
                    state = state,
                    foldingFeature = windowLayout.foldingFeature,
                    onEvent = onEvent,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
        }
    }
}

