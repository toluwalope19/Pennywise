package com.example.dashboard

import androidx.lifecycle.viewModelScope
import com.example.common.mvi.MviViewModel
import com.example.domain.model.TransactionType
import com.example.domain.usecase.category.GetCategoriesUseCase
import com.example.domain.usecase.transaction.GetAllTimeBalanceUseCase
import com.example.domain.usecase.transaction.GetMonthlyTransactionsUseCase
import com.example.domain.usecase.transaction.GetRecentTransactionsUseCase
import com.example.domain.usecase.transaction.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.Month
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getRecentTransactions: GetRecentTransactionsUseCase,
    private val getMonthlyTransactions: GetMonthlyTransactionsUseCase,
    private val getCategories: GetCategoriesUseCase,
    private val getAllTimeBalance: GetAllTimeBalanceUseCase
) : MviViewModel<DashboardUiState, DashboardUiEvent, DashboardUiEffect>(
    initialState = DashboardUiState()
) {
    init {
        onEvent(DashboardUiEvent.LoadDashboard)
    }

    override fun handleEvent(event: DashboardUiEvent) {
        when (event) {
            DashboardUiEvent.LoadDashboard -> loadDashboard()
            is DashboardUiEvent.OnMonthChanged -> changeMonth(event.direction)
            is DashboardUiEvent.OnTransactionClick -> {
                setEffect(DashboardUiEffect.NavigateToTransaction(event.id))
            }
            DashboardUiEvent.OnSeeAllTransactionsClick -> {
                setEffect(DashboardUiEffect.NavigateToTransactions)
            }
            DashboardUiEvent.OnAddTransactionClick -> {
                setEffect(DashboardUiEffect.NavigateToAddTransaction())
            }
            DashboardUiEvent.OnSettingsClick -> {
                setEffect(DashboardUiEffect.NavigateToSettings)
            }
        }
    }

    private fun loadDashboard() {
        viewModelScope.launch {

            launch {
                getAllTimeBalance().collect { balance ->
                    setState {
                        copy(totalBalance = balance)

                    }
                }
            }

                    combine(
                getMonthlyTransactions(
                    currentState.selectedMonth,
                    currentState.selectedYear
                ),
                getRecentTransactions(limit = 5),
                getCategories()
            ) { monthlyTransactions, recentTransactions, categories ->


                val categoryMap = categories.associateBy { it.id }

                val totalIncome = monthlyTransactions
                    .filter { it.type == TransactionType.INCOME }
                    .sumOf { it.amount }

                val totalExpense = monthlyTransactions
                    .filter { it.type == TransactionType.EXPENSE }
                    .sumOf { it.amount }

                val categorySpending = monthlyTransactions
                    .filter { it.type == TransactionType.EXPENSE }
                    .groupBy { it.categoryId }
                    .map { (categoryId, txns) ->
                        val amount = txns.sumOf { it.amount }
                        CategorySpending(
                            categoryName = categoryMap[categoryId]?.name ?: "Other",
                            amount = amount,
                            percentage = if (totalExpense > 0)
                                (amount / totalExpense * 100).toFloat()
                            else 0f
                        )
                    }
                    .sortedByDescending { it.amount }

                // Now returns a named data class — readable, no confusion
                DashboardMonthlyData(
                    totalIncome = totalIncome,
                    totalExpense = totalExpense,
                    categorySpending = categorySpending,
                    recentTransactions = recentTransactions,
                    categoryMap = categoryMap
                )
            }
                .catch { e ->
                    setState { copy(isLoading = false, error = e.message) }
                }
                .collect { data ->
                    // Named fields — crystal clear what each value is
                    setState {
                        copy(
                            categoryMap = data.categoryMap,
                            isLoading = false,
                            totalIncome = data.totalIncome,
                            totalExpense = data.totalExpense,
                            spendingByCategory = data.categorySpending,
                            recentTransactions = data.recentTransactions
                        )
                    }
                }
        }
    }

    private fun changeMonth(direction: Int) {
        val current = java.time.YearMonth.of(
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
        loadDashboard()
    }
}