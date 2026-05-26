package com.example.transactions.list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.transactions.components.TransactionsContent
import com.example.ui.PennywiseWindowLayout

@Composable
fun TransactionsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToTransaction: (Long) -> Unit,
    onNavigateToAddTransaction: () -> Unit,
    windowLayout: PennywiseWindowLayout = PennywiseWindowLayout.PhonePortrait,
    viewModel: TransactionsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val transactions = viewModel.transactions.collectAsLazyPagingItems()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                TransactionsUiEffect.NavigateBack -> onNavigateBack()
                is TransactionsUiEffect.NavigateToTransaction ->
                    onNavigateToTransaction(effect.id)
                TransactionsUiEffect.NavigateToAddTransaction ->
                    onNavigateToAddTransaction()
            }
        }
    }

    val isMasterDetail = windowLayout is PennywiseWindowLayout.TabletLandscape ||
            windowLayout is PennywiseWindowLayout.Foldable

    if (isMasterDetail) {
        TransactionsMasterDetailLayout(
            state = state,
            transactions = transactions,
            onEvent = viewModel::onEvent,
            modifier = Modifier.fillMaxSize()
        )
    } else {
        TransactionsContent(
            state = state,
            transactions = transactions,
            onEvent = viewModel::onEvent,
            horizontalPadding = if (windowLayout is PennywiseWindowLayout.TabletPortrait) {
                24.dp
            } else {
                16.dp
            }
        )
    }
}