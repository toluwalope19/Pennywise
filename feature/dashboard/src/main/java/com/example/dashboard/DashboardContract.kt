package com.example.dashboard

import com.example.domain.model.Category
import com.example.domain.model.Transaction
import java.time.LocalDate


data class DashboardUiState(
    val categoryMap: Map<Long, Category> = emptyMap(),
    val isLoading: Boolean = false,
    val userName: String = "Alex Park",
    val selectedMonth: Int = LocalDate.now().monthValue,
    val selectedYear: Int = LocalDate.now().year,
    val totalBalance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val spendingByCategory: List<CategorySpending> = emptyList(),
    val recentTransactions: List<Transaction> = emptyList(),
    val error: String? = null
)

data class CategorySpending(
    val categoryName: String,
    val amount: Double,
    val percentage: Float
)

data class DashboardMonthlyData(
    val totalIncome: Double,
    val totalExpense: Double,
    val categorySpending: List<CategorySpending>,
    val recentTransactions: List<Transaction>,
    val categoryMap: Map<Long, Category>
)

// ── Events ────────────────────────────────────────────────────────────────

sealed class DashboardUiEvent {
    data object LoadDashboard : DashboardUiEvent()
    data class OnMonthChanged(val direction: Int) : DashboardUiEvent() // -1 or +1
    data class OnTransactionClick(val id: Long) : DashboardUiEvent()
    data object OnSeeAllTransactionsClick : DashboardUiEvent()
    data object OnAddTransactionClick : DashboardUiEvent()
}

// ── Effects ───────────────────────────────────────────────────────────────

sealed class DashboardUiEffect {
    data object NavigateToTransactions : DashboardUiEffect()
    data class NavigateToTransaction(val id: Long) : DashboardUiEffect()
    data class NavigateToAddTransaction(val type: String = "EXPENSE") : DashboardUiEffect()
}