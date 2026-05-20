package com.example.budgets



import androidx.lifecycle.viewModelScope
import com.example.common.mvi.MviViewModel
import com.example.domain.model.Budget
import com.example.domain.model.TransactionType
import com.example.domain.usecase.budget.AddBudgetUseCase
import com.example.domain.usecase.budget.GetBudgetsByMonthUseCase
import com.example.domain.usecase.category.GetCategoriesUseCase
import com.example.domain.usecase.transaction.GetMonthlyTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class BudgetsViewModel @Inject constructor(
    private val getBudgetsByMonth: GetBudgetsByMonthUseCase,
    private val getMonthlyTransactions: GetMonthlyTransactionsUseCase,
    private val getCategories: GetCategoriesUseCase,
    private val addBudget: AddBudgetUseCase
) : MviViewModel<BudgetsUiState, BudgetsUiEvent, BudgetsUiEffect>(
    initialState = BudgetsUiState()
) {
    // New budget sheet state — separate from main UI state
    private var newBudgetState = NewBudgetState()

    init {
        loadBudgets()
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            getCategories().collect { categories ->
                newBudgetState = newBudgetState.copy(
                    availableCategories = categories,
                    selectedCategory = newBudgetState.selectedCategory
                        ?: categories.firstOrNull()
                )
            }
        }
    }

    private fun loadBudgets() {
        viewModelScope.launch {
            combine(
                getBudgetsByMonth(
                    currentState.selectedMonth,
                    currentState.selectedYear
                ),
                getMonthlyTransactions(
                    currentState.selectedMonth,
                    currentState.selectedYear
                ),
                getCategories()
            ) { budgets, transactions, categories ->
                val categoryMap = categories.associateBy { it.id }

                // Build spending map per category
                val spendingMap = transactions
                    .filter { it.type == TransactionType.EXPENSE }
                    .groupBy { it.categoryId }
                    .mapValues { (_, txns) -> txns.sumOf { it.amount } }

                // Map budgets to BudgetWithSpending
                val budgetsWithSpending = budgets.map { budget ->
                    val spent = spendingMap[budget.categoryId] ?: 0.0
                    val percentage = if (budget.amount > 0) {
                        (spent / budget.amount).toFloat()
                    } else 0f

                    BudgetWithSpending(
                        budget = budget,
                        category = categoryMap[budget.categoryId],
                        spent = spent,
                        percentage = percentage,
                        isOverBudget = spent > budget.amount
                    )
                }

                val totalBudget = budgets.sumOf { it.amount }
                val totalSpent = spendingMap.values.sum()

                Triple(budgetsWithSpending, totalBudget, totalSpent)
            }
                .catch { e ->
                    setState { copy(isLoading = false, error = e.message) }
                }
                .collect { (budgets, totalBudget, totalSpent) ->
                    setState {
                        copy(
                            isLoading = false,
                            budgets = budgets,
                            totalBudget = totalBudget,
                            totalSpent = totalSpent
                        )
                    }
                }
        }
    }

    override fun handleEvent(event: BudgetsUiEvent) {
        when (event) {
            BudgetsUiEvent.OnAddBudgetClick -> setState {
                copy(showNewBudgetSheet = true)
            }
            BudgetsUiEvent.OnNewBudgetDismiss -> setState {
                copy(showNewBudgetSheet = false)
            }
            is BudgetsUiEvent.OnMonthChanged -> changeMonth(event.direction)
            is BudgetsUiEvent.OnBudgetClick -> setEffect(
                BudgetsUiEffect.NavigateToBudgetDetail(event.budgetId)
            )

            // New budget sheet
            is BudgetsUiEvent.OnAmountDigitPressed -> handleDigit(event.digit)
            BudgetsUiEvent.OnAmountDeletePressed -> handleDelete()
            BudgetsUiEvent.OnAmountDecimalPressed -> handleDecimal()
            is BudgetsUiEvent.OnQuickAmountPressed -> {
                newBudgetState = newBudgetState.copy(
                    amountInput = event.amount.toBigDecimal()
                        .stripTrailingZeros().toPlainString()
                )
            }
            BudgetsUiEvent.OnCategoryPickerOpen -> {
                newBudgetState = newBudgetState.copy(showCategoryPicker = true)
            }
            BudgetsUiEvent.OnCategoryPickerDismiss -> {
                newBudgetState = newBudgetState.copy(showCategoryPicker = false)
            }
            is BudgetsUiEvent.OnCategorySelected -> {
                newBudgetState = newBudgetState.copy(
                    selectedCategory = event.category,
                    showCategoryPicker = false
                )
            }
            is BudgetsUiEvent.OnPeriodChanged -> {
                newBudgetState = newBudgetState.copy(period = event.period)
            }
            BudgetsUiEvent.OnDatePickerOpen -> {
                newBudgetState = newBudgetState.copy(showDatePicker = true)
            }
            BudgetsUiEvent.OnDatePickerDismiss -> {
                newBudgetState = newBudgetState.copy(showDatePicker = false)
            }
            is BudgetsUiEvent.OnDateSelected -> {
                newBudgetState = newBudgetState.copy(
                    startDate = event.date,
                    showDatePicker = false
                )
            }
            is BudgetsUiEvent.OnAlertsToggled -> {
                newBudgetState = newBudgetState.copy(alertsEnabled = event.enabled)
            }
            is BudgetsUiEvent.OnAlertThresholdChanged -> {
                newBudgetState = newBudgetState.copy(alertThreshold = event.threshold)
            }
            BudgetsUiEvent.OnCreateBudgetClicked -> createBudget()
        }
    }

    private fun handleDigit(digit: String) {
        val current = newBudgetState.amountInput
        val new = when {
            current == "0" -> digit
            current.contains(".") &&
                    current.substringAfter(".").length >= 2 -> current
            current.replace(".", "").length >= 10 -> current
            else -> current + digit
        }
        newBudgetState = newBudgetState.copy(amountInput = new)
    }

    private fun handleDelete() {
        val current = newBudgetState.amountInput
        newBudgetState = newBudgetState.copy(
            amountInput = if (current.length <= 1) "0" else current.dropLast(1)
        )
    }

    private fun handleDecimal() {
        val current = newBudgetState.amountInput
        if (!current.contains(".")) {
            newBudgetState = newBudgetState.copy(amountInput = "$current.")
        }
    }

    private fun changeMonth(direction: Int) {
        val current = YearMonth.of(currentState.selectedYear, currentState.selectedMonth)
        val new = current.plusMonths(direction.toLong())
        setState { copy(selectedMonth = new.monthValue, selectedYear = new.year) }
        loadBudgets()
    }

    private fun createBudget() {
        val state = newBudgetState
        if (state.amount <= 0.0) {
            setEffect(BudgetsUiEffect.ShowError("Please enter a budget amount"))
            return
        }
        if (state.selectedCategory == null) {
            setEffect(BudgetsUiEffect.ShowError("Please select a category"))
            return
        }

        viewModelScope.launch {
            newBudgetState = newBudgetState.copy(isSaving = true)
            try {
                addBudget(
                    Budget(
                        categoryId = state.selectedCategory.id,
                        amount = state.amount,
                        month = state.startDate.monthValue,
                        year = state.startDate.year,
                        period = state.period,
                        startDay = state.startDate.dayOfMonth,
                        alertsEnabled = state.alertsEnabled,
                        alertThreshold = state.alertThreshold
                    )
                )
                newBudgetState = NewBudgetState() // reset sheet
                setState { copy(showNewBudgetSheet = false) }
            } catch (e: Exception) {
                newBudgetState = newBudgetState.copy(isSaving = false)
                setEffect(BudgetsUiEffect.ShowError(
                    e.message ?: "Failed to create budget"
                ))
            }
        }
    }

    // Expose newBudgetState to UI
    fun getNewBudgetState() = newBudgetState
}