package com.example.budgets



import com.example.domain.model.Budget
import com.example.domain.model.BudgetPeriod
import com.example.domain.model.Category
import java.time.LocalDate
import java.time.YearMonth

// ── Budget with spending data ──────────────────────────────────────────────

data class BudgetWithSpending(
    val budget: Budget,
    val category: Category?,
    val spent: Double,
    val percentage: Float, // spent / budget.amount
    val isOverBudget: Boolean
)

// ── UI State ──────────────────────────────────────────────────────────────

data class BudgetsUiState(
    val isLoading: Boolean = true,
    val selectedMonth: Int = YearMonth.now().monthValue,
    val selectedYear: Int = YearMonth.now().year,
    val totalBudget: Double = 0.0,
    val totalSpent: Double = 0.0,
    val budgets: List<BudgetWithSpending> = emptyList(),
    val error: String? = null,
    val showNewBudgetSheet: Boolean = false
)

val BudgetsUiState.remainingBudget: Double
    get() = totalBudget - totalSpent

val BudgetsUiState.overallPercentage: Float
    get() = if (totalBudget > 0) (totalSpent / totalBudget).toFloat() else 0f

// ── New Budget Sheet State ─────────────────────────────────────────────────

data class NewBudgetState(
    val amountInput: String = "0",
    val selectedCategory: Category? = null,
    val availableCategories: List<Category> = emptyList(),
    val period: BudgetPeriod = BudgetPeriod.MONTHLY,
    val startDate: LocalDate = LocalDate.now().withDayOfMonth(1), // defaults to 1st of next month
    val alertsEnabled: Boolean = true,
    val alertThreshold: Float = 0.8f,
    val showCategoryPicker: Boolean = false,
    val showDatePicker: Boolean = false,
    val isSaving: Boolean = false
)

val NewBudgetState.amount: Double
    get() = amountInput.toDoubleOrNull() ?: 0.0

// ── Events ────────────────────────────────────────────────────────────────

sealed class BudgetsUiEvent {
    data object OnAddBudgetClick : BudgetsUiEvent()
    data object OnNewBudgetDismiss : BudgetsUiEvent()
    data class OnMonthChanged(val direction: Int) : BudgetsUiEvent()
    data class OnBudgetClick(val budgetId: Long) : BudgetsUiEvent()

    // New budget sheet events
    data class OnAmountDigitPressed(val digit: String) : BudgetsUiEvent()
    data object OnAmountDeletePressed : BudgetsUiEvent()
    data object OnAmountDecimalPressed : BudgetsUiEvent()
    data class OnQuickAmountPressed(val amount: Double) : BudgetsUiEvent()
    data object OnCategoryPickerOpen : BudgetsUiEvent()
    data object OnCategoryPickerDismiss : BudgetsUiEvent()
    data class OnCategorySelected(val category: Category) : BudgetsUiEvent()
    data class OnPeriodChanged(val period: BudgetPeriod) : BudgetsUiEvent()
    data object OnDatePickerOpen : BudgetsUiEvent()
    data object OnDatePickerDismiss : BudgetsUiEvent()
    data class OnDateSelected(val date: LocalDate) : BudgetsUiEvent()
    data class OnAlertsToggled(val enabled: Boolean) : BudgetsUiEvent()
    data class OnAlertThresholdChanged(val threshold: Float) : BudgetsUiEvent()
    data object OnCreateBudgetClicked : BudgetsUiEvent()
}

// ── Effects ────────────────────────────────────────────────────────────────

sealed class BudgetsUiEffect {
    data class NavigateToBudgetDetail(val budgetId: Long) : BudgetsUiEffect()
    data class ShowError(val message: String) : BudgetsUiEffect()
}