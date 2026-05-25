package com.example.transactions.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
    windowLayout: PennywiseWindowLayout,
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

    TransactionsContent(
        state = state,
        transactions = transactions,
        onEvent = viewModel::onEvent
    )
}