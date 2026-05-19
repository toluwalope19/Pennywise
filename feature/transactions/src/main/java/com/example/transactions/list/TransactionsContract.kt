package com.example.transactions.list


import com.example.domain.model.Category
import com.example.domain.model.Transaction
import java.time.LocalDate
import java.time.LocalDate.now

// ── Sealed class for paged list items ─────────────────────────────────────
// This is what insertSeparators() produces — either a header or a transaction

sealed class TransactionListItem {
    data class Header(val date: LocalDate) : TransactionListItem()
    data class Item(val transaction: Transaction) : TransactionListItem()
}

// ── UI State ───────────────────────────────────────────────────────────────

data class TransactionsUiState(
    val isLoading: Boolean = true,
    val selectedMonth: Int = now().monthValue,
    val selectedYear: Int = now().year,
    val selectedFilter: TransactionFilter = TransactionFilter.ALL,
    val categoryMap: Map<Long, Category> = emptyMap(),
    val error: String? = null
)

enum class TransactionFilter {
    ALL, INCOME, EXPENSE;

    fun displayName() = name
        .lowercase()
        .replaceFirstChar { it.uppercase() }
}

// ── Events ─────────────────────────────────────────────────────────────────

sealed class TransactionsUiEvent {
    data object LoadTransactions : TransactionsUiEvent()
    data class OnFilterChanged(val filter: TransactionFilter) : TransactionsUiEvent()
    data class OnMonthChanged(val direction: Int) : TransactionsUiEvent()
    data class OnTransactionClick(val id: Long) : TransactionsUiEvent()
    data object OnAddTransactionClick : TransactionsUiEvent()
    data object OnBackClick : TransactionsUiEvent()
}

// ── Effects ────────────────────────────────────────────────────────────────

sealed class TransactionsUiEffect {
    data object NavigateBack : TransactionsUiEffect()
    data class NavigateToTransaction(val id: Long) : TransactionsUiEffect()
    data object NavigateToAddTransaction : TransactionsUiEffect()
}