package com.example.transactions



import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.insertSeparators
import androidx.paging.map
import com.example.common.mvi.MviViewModel
import com.example.domain.model.TransactionType
import com.example.domain.usecase.transaction.GetTransactionsUseCase

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val getTransactions: GetTransactionsUseCase
) : MviViewModel<TransactionsUiState, TransactionsUiEvent, TransactionsUiEffect>(
    initialState = TransactionsUiState()
) {
    // Trigger flow — emits whenever month or filter changes
    private val queryParams = MutableStateFlow(
        Pair(
            YearMonth.of(
                TransactionsUiState().selectedYear,
                TransactionsUiState().selectedMonth
            ),
            TransactionFilter.ALL
        )
    )

    // The paged + separated list — collected directly by the UI
    @OptIn(ExperimentalCoroutinesApi::class)
    val transactions: Flow<PagingData<TransactionListItem>> = queryParams
        .flatMapLatest { (yearMonth, filter) ->
            getTransactions
                .byMonth(yearMonth.monthValue, yearMonth.year)
                .map { pagingData ->
                    // Step 1 — filter by type if needed
                    val filtered = when (filter) {
                        TransactionFilter.ALL -> pagingData
                        TransactionFilter.INCOME -> pagingData.filter {
                            it.type == TransactionType.INCOME
                        }
                        TransactionFilter.EXPENSE -> pagingData.filter {
                            it.type == TransactionType.EXPENSE
                        }
                    }

                    // Step 2 — wrap each transaction in TransactionListItem.Item
                    val asItems = filtered.map { transaction ->
                        TransactionListItem.Item(transaction) as TransactionListItem
                    }

                    // Step 3 — insertSeparators injects date headers
                    // between adjacent items that have different dates
                    asItems.insertSeparators { before, after ->
                        when {
                            // First item — insert header before it
                            before == null && after is TransactionListItem.Item -> {
                                TransactionListItem.Header(after.transaction.date)
                            }
                            // Adjacent items with different dates — insert header
                            before is TransactionListItem.Item &&
                                    after is TransactionListItem.Item &&
                                    before.transaction.date != after.transaction.date -> {
                                TransactionListItem.Header(after.transaction.date)
                            }
                            // Same date or last item — no separator
                            else -> null
                        }
                    }
                }
        }
        .cachedIn(viewModelScope) // ← survives rotation

    override fun handleEvent(event: TransactionsUiEvent) {
        when (event) {
            TransactionsUiEvent.LoadTransactions -> Unit // auto-loads via queryParams
            is TransactionsUiEvent.OnFilterChanged -> {
                setState { copy(selectedFilter = event.filter) }
                queryParams.value = Pair(
                    YearMonth.of(currentState.selectedYear, currentState.selectedMonth),
                    event.filter
                )
            }
            is TransactionsUiEvent.OnMonthChanged -> changeMonth(event.direction)
            is TransactionsUiEvent.OnTransactionClick -> {
                setEffect(TransactionsUiEffect.NavigateToTransaction(event.id))
            }
            TransactionsUiEvent.OnAddTransactionClick -> {
                setEffect(TransactionsUiEffect.NavigateToAddTransaction)
            }
            TransactionsUiEvent.OnBackClick -> {
                setEffect(TransactionsUiEffect.NavigateBack)
            }
        }
    }

    private fun changeMonth(direction: Int) {
        val current = YearMonth.of(
            currentState.selectedYear,
            currentState.selectedMonth
        )
        val new = current.plusMonths(direction.toLong())
        setState {
            copy(
                selectedMonth = new.monthValue,
                selectedYear = new.year
            )
        }
        queryParams.value = Pair(new, currentState.selectedFilter)
    }
}