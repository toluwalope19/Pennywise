package com.example.analytics



import androidx.lifecycle.viewModelScope
import com.example.common.mvi.MviViewModel
import com.example.domain.model.TransactionType
import com.example.domain.usecase.category.GetCategoriesUseCase
import com.example.domain.usecase.transaction.ExportCsvUseCase
import com.example.domain.usecase.transaction.GetTransactionsInRangeUseCase
import com.example.ui.components.CategoryType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val getTransactionsInRange: GetTransactionsInRangeUseCase,
    private val getCategories: GetCategoriesUseCase,
    private val exportCsv: ExportCsvUseCase
) : MviViewModel<AnalyticsUiState, AnalyticsUiEvent, AnalyticsUiEffect>(
    initialState = AnalyticsUiState()
) {
    init {
        onEvent(AnalyticsUiEvent.LoadAnalytics)
    }

    override fun handleEvent(event: AnalyticsUiEvent) {
        when (event) {
            AnalyticsUiEvent.LoadAnalytics -> loadAnalytics()
            AnalyticsUiEvent.OnExportClicked -> {
                exportTransactions()
            }
        }
    }

    private fun exportTransactions() {
        viewModelScope.launch {
            setState { copy(isExporting = true) }
            try {
                val csv = exportCsv()
                setState { copy(isExporting = false) }
                setEffect(AnalyticsUiEffect.ExportCSV(csv))
            } catch (e: Exception) {
                setState { copy(isExporting = false) }
                setEffect(AnalyticsUiEffect.ShowError("Export failed: ${e.message}"))
            }
        }
    }

    private fun loadAnalytics() {
        viewModelScope.launch {
            // Last 6 months date range
            val endDate = LocalDate.now()
            val startDate = LocalDate.now()
                .minusMonths(5)
                .withDayOfMonth(1)

            combine(
                getTransactionsInRange(startDate, endDate),
                getCategories()
            ) { transactions, categories ->

                val categoryMap = categories.associateBy { it.id }

                // Group by YearMonth
                val grouped = transactions.groupBy {
                    YearMonth.of(it.date.year, it.date.monthValue)
                }

                // Build last 6 months — even if no transactions
                val last6Months = (5 downTo 0).map { monthsAgo ->
                    YearMonth.now().minusMonths(monthsAgo.toLong())
                }

                val monthlyData = last6Months.map { yearMonth ->
                    val monthTxns = grouped[yearMonth] ?: emptyList()
                    MonthlyData(
                        yearMonth = yearMonth,
                        income = monthTxns
                            .filter { it.type == TransactionType.INCOME }
                            .sumOf { it.amount },
                        expense = monthTxns
                            .filter { it.type == TransactionType.EXPENSE }
                            .sumOf { it.amount }
                    )
                }

                // 6-month totals
                val totalIncome = monthlyData.sumOf { it.income }
                val totalExpense = monthlyData.sumOf { it.expense }

                // Current month spending breakdown
                val currentMonth = YearMonth.now()
                val currentMonthTxns = grouped[currentMonth] ?: emptyList()
                val currentExpenses = currentMonthTxns
                    .filter { it.type == TransactionType.EXPENSE }
                val totalCurrentExpense = currentExpenses.sumOf { it.amount }

                val breakdown = currentExpenses
                    .groupBy { it.categoryId }
                    .map { (categoryId, txns) ->
                        val amount = txns.sumOf { it.amount }
                        val category = categoryMap[categoryId]
                        val categoryType = CategoryType.fromName(
                            category?.name ?: "Other"
                        )
                        CategoryBreakdown(
                            categoryName = category?.name ?: "Other",
                            color = categoryType.gradientStart.value.toLong(),
                            amount = amount,
                            percentage = if (totalCurrentExpense > 0) {
                                (amount / totalCurrentExpense * 100).toFloat()
                            } else 0f
                        )
                    }
                    .sortedByDescending { it.amount }

                Triple(monthlyData, Pair(totalIncome, totalExpense), breakdown)
            }
                .catch { e ->
                    setState { copy(isLoading = false, error = e.message) }
                }
                .collect { (monthlyData, totals, breakdown) ->
                    setState {
                        copy(
                            isLoading = false,
                            monthlyData = monthlyData,
                            totalIncome6Months = totals.first,
                            totalExpense6Months = totals.second,
                            spendingBreakdown = breakdown
                        )
                    }
                }
        }
    }
}