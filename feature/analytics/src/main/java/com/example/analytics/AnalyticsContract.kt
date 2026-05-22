package com.example.analytics


import java.time.YearMonth

data class MonthlyData(
    val yearMonth: YearMonth,
    val income: Double,
    val expense: Double
)

data class CategoryBreakdown(
    val categoryName: String,
    val color: Long, // stored as color int
    val amount: Double,
    val percentage: Float
)

data class AnalyticsUiState(
    val isLoading: Boolean = true,
    val monthlyData: List<MonthlyData> = emptyList(),
    val totalIncome6Months: Double = 0.0,
    val totalExpense6Months: Double = 0.0,
    val spendingBreakdown: List<CategoryBreakdown> = emptyList(),
    val selectedMonth: Int = YearMonth.now().monthValue,
    val selectedYear: Int = YearMonth.now().year,
    val error: String? = null
)

sealed class AnalyticsUiEvent {
    data object LoadAnalytics : AnalyticsUiEvent()
    data object OnExportClicked : AnalyticsUiEvent()
}

sealed class AnalyticsUiEffect {
    data object ExportCSV : AnalyticsUiEffect()
    data class ShowError(val message: String) : AnalyticsUiEffect()
}