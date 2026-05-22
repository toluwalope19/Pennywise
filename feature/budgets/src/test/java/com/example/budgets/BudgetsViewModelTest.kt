package com.example.budgets

import app.cash.turbine.test
import com.example.domain.model.Budget
import com.example.domain.model.BudgetPeriod
import com.example.domain.model.Category
import com.example.domain.model.Transaction
import com.example.domain.model.TransactionType
import com.example.domain.usecase.budget.AddBudgetUseCase
import com.example.domain.usecase.budget.GetBudgetsByMonthUseCase
import com.example.domain.usecase.category.GetCategoriesUseCase
import com.example.domain.usecase.transaction.GetMonthlyTransactionsUseCase
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalCoroutinesApi::class)
class BudgetsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var getBudgetsByMonth: GetBudgetsByMonthUseCase
    private lateinit var getMonthlyTransactions: GetMonthlyTransactionsUseCase
    private lateinit var getCategories: GetCategoriesUseCase
    private lateinit var addBudget: AddBudgetUseCase
    private lateinit var viewModel: BudgetsViewModel

    private val sampleCategory = Category(1L, "Food", "restaurant", "#FF5733")
    private val sampleBudget = Budget(1L, 1L, 500.0, 5, 2024, BudgetPeriod.MONTHLY)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getBudgetsByMonth = mockk()
        getMonthlyTransactions = mockk()
        getCategories = mockk()
        addBudget = mockk()

        every { getBudgetsByMonth(any(), any()) } returns flowOf(emptyList())
        every { getMonthlyTransactions(any(), any()) } returns flowOf(emptyList())
        every { getCategories() } returns flowOf(listOf(sampleCategory))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = BudgetsViewModel(
        getBudgetsByMonth, getMonthlyTransactions, getCategories, addBudget
    )

    @Test
    fun `initial state has current month and year`() {
        viewModel = createViewModel()
        val now = YearMonth.now()
        assertEquals(now.monthValue, viewModel.state.value.selectedMonth)
        assertEquals(now.year, viewModel.state.value.selectedYear)
    }

    @Test
    fun `initial state showNewBudgetSheet is false`() {
        viewModel = createViewModel()
        assertFalse(viewModel.state.value.showNewBudgetSheet)
    }

    @Test
    fun `OnAddBudgetClick sets showNewBudgetSheet true`() {
        viewModel = createViewModel()
        viewModel.onEvent(BudgetsUiEvent.OnAddBudgetClick)
        assertTrue(viewModel.state.value.showNewBudgetSheet)
    }

    @Test
    fun `OnNewBudgetDismiss sets showNewBudgetSheet false`() {
        viewModel = createViewModel()
        viewModel.onEvent(BudgetsUiEvent.OnAddBudgetClick)
        viewModel.onEvent(BudgetsUiEvent.OnNewBudgetDismiss)
        assertFalse(viewModel.state.value.showNewBudgetSheet)
    }

    @Test
    fun `OnBudgetClick emits NavigateToBudgetDetail effect`() = runTest {
        viewModel = createViewModel()

        viewModel.effect.test {
            viewModel.onEvent(BudgetsUiEvent.OnBudgetClick(55L))
            val effect = awaitItem()
            assert(effect is BudgetsUiEffect.NavigateToBudgetDetail)
            assertEquals(55L, (effect as BudgetsUiEffect.NavigateToBudgetDetail).budgetId)
        }
    }

    @Test
    fun `OnMonthChanged increments month`() {
        viewModel = createViewModel()
        val initial = YearMonth.now()

        viewModel.onEvent(BudgetsUiEvent.OnMonthChanged(1))

        val expected = initial.plusMonths(1)
        assertEquals(expected.monthValue, viewModel.state.value.selectedMonth)
        assertEquals(expected.year, viewModel.state.value.selectedYear)
    }

    @Test
    fun `OnMonthChanged decrements month`() {
        viewModel = createViewModel()

        viewModel.onEvent(BudgetsUiEvent.OnMonthChanged(-1))

        val expected = YearMonth.now().minusMonths(1)
        assertEquals(expected.monthValue, viewModel.state.value.selectedMonth)
        assertEquals(expected.year, viewModel.state.value.selectedYear)
    }

    @Test
    fun `OnAmountDigitPressed appends digit`() {
        viewModel = createViewModel()
        viewModel.onEvent(BudgetsUiEvent.OnAmountDigitPressed("5"))
        assertEquals("5", viewModel.newBudgetState.value.amountInput)
    }

    @Test
    fun `OnAmountDigitPressed replaces leading zero`() {
        viewModel = createViewModel()
        assertEquals("0", viewModel.newBudgetState.value.amountInput)
        viewModel.onEvent(BudgetsUiEvent.OnAmountDigitPressed("3"))
        assertEquals("3", viewModel.newBudgetState.value.amountInput)
    }

    @Test
    fun `OnAmountDigitPressed builds multi-digit amount`() {
        viewModel = createViewModel()
        viewModel.onEvent(BudgetsUiEvent.OnAmountDigitPressed("1"))
        viewModel.onEvent(BudgetsUiEvent.OnAmountDigitPressed("2"))
        viewModel.onEvent(BudgetsUiEvent.OnAmountDigitPressed("3"))
        assertEquals("123", viewModel.newBudgetState.value.amountInput)
    }

    @Test
    fun `OnAmountDeletePressed reduces to 0 when single digit`() {
        viewModel = createViewModel()
        viewModel.onEvent(BudgetsUiEvent.OnAmountDigitPressed("5"))
        viewModel.onEvent(BudgetsUiEvent.OnAmountDeletePressed)
        assertEquals("0", viewModel.newBudgetState.value.amountInput)
    }

    @Test
    fun `OnAmountDeletePressed removes last digit`() {
        viewModel = createViewModel()
        viewModel.onEvent(BudgetsUiEvent.OnAmountDigitPressed("1"))
        viewModel.onEvent(BudgetsUiEvent.OnAmountDigitPressed("2"))
        viewModel.onEvent(BudgetsUiEvent.OnAmountDeletePressed)
        assertEquals("1", viewModel.newBudgetState.value.amountInput)
    }

    @Test
    fun `OnAmountDecimalPressed adds decimal point`() {
        viewModel = createViewModel()
        viewModel.onEvent(BudgetsUiEvent.OnAmountDigitPressed("5"))
        viewModel.onEvent(BudgetsUiEvent.OnAmountDecimalPressed)
        assertEquals("5.", viewModel.newBudgetState.value.amountInput)
    }

    @Test
    fun `OnAmountDecimalPressed is idempotent`() {
        viewModel = createViewModel()
        viewModel.onEvent(BudgetsUiEvent.OnAmountDecimalPressed)
        viewModel.onEvent(BudgetsUiEvent.OnAmountDecimalPressed)
        assertEquals("0.", viewModel.newBudgetState.value.amountInput)
    }

    @Test
    fun `OnAmountDigitPressed limits to 2 decimal places`() {
        viewModel = createViewModel()
        viewModel.onEvent(BudgetsUiEvent.OnAmountDecimalPressed)
        viewModel.onEvent(BudgetsUiEvent.OnAmountDigitPressed("1"))
        viewModel.onEvent(BudgetsUiEvent.OnAmountDigitPressed("2"))
        viewModel.onEvent(BudgetsUiEvent.OnAmountDigitPressed("3"))
        assertEquals("0.12", viewModel.newBudgetState.value.amountInput)
    }

    @Test
    fun `OnQuickAmountPressed sets amountInput correctly`() {
        viewModel = createViewModel()
        viewModel.onEvent(BudgetsUiEvent.OnQuickAmountPressed(1000.0))
        assertEquals("1000", viewModel.newBudgetState.value.amountInput)
    }

    @Test
    fun `OnCategoryPickerOpen shows picker`() {
        viewModel = createViewModel()
        viewModel.onEvent(BudgetsUiEvent.OnCategoryPickerOpen)
        assertTrue(viewModel.newBudgetState.value.showCategoryPicker)
    }

    @Test
    fun `OnCategoryPickerDismiss hides picker`() {
        viewModel = createViewModel()
        viewModel.onEvent(BudgetsUiEvent.OnCategoryPickerOpen)
        viewModel.onEvent(BudgetsUiEvent.OnCategoryPickerDismiss)
        assertFalse(viewModel.newBudgetState.value.showCategoryPicker)
    }

    @Test
    fun `OnCategorySelected updates selectedCategory`() {
        val category = Category(2L, "Shopping", "shopping", "#3498DB")
        viewModel = createViewModel()
        viewModel.onEvent(BudgetsUiEvent.OnCategorySelected(category))
        assertEquals(category, viewModel.newBudgetState.value.selectedCategory)
        assertFalse(viewModel.newBudgetState.value.showCategoryPicker)
    }

    @Test
    fun `OnPeriodChanged updates period`() {
        viewModel = createViewModel()
        viewModel.onEvent(BudgetsUiEvent.OnPeriodChanged(BudgetPeriod.WEEKLY))
        assertEquals(BudgetPeriod.WEEKLY, viewModel.newBudgetState.value.period)
    }

    @Test
    fun `OnDatePickerOpen shows date picker`() {
        viewModel = createViewModel()
        viewModel.onEvent(BudgetsUiEvent.OnDatePickerOpen)
        assertTrue(viewModel.newBudgetState.value.showDatePicker)
    }

    @Test
    fun `OnDatePickerDismiss hides date picker`() {
        viewModel = createViewModel()
        viewModel.onEvent(BudgetsUiEvent.OnDatePickerOpen)
        viewModel.onEvent(BudgetsUiEvent.OnDatePickerDismiss)
        assertFalse(viewModel.newBudgetState.value.showDatePicker)
    }

    @Test
    fun `OnDateSelected updates startDate and hides picker`() {
        val date = LocalDate.of(2024, 6, 1)
        viewModel = createViewModel()
        viewModel.onEvent(BudgetsUiEvent.OnDateSelected(date))
        assertEquals(date, viewModel.newBudgetState.value.startDate)
        assertFalse(viewModel.newBudgetState.value.showDatePicker)
    }

    @Test
    fun `OnAlertsToggled updates alertsEnabled`() {
        viewModel = createViewModel()
        viewModel.onEvent(BudgetsUiEvent.OnAlertsToggled(false))
        assertFalse(viewModel.newBudgetState.value.alertsEnabled)
    }

    @Test
    fun `OnAlertThresholdChanged updates alertThreshold`() {
        viewModel = createViewModel()
        viewModel.onEvent(BudgetsUiEvent.OnAlertThresholdChanged(0.5f))
        assertEquals(0.5f, viewModel.newBudgetState.value.alertThreshold)
    }

    @Test
    fun `OnCreateBudgetClicked with zero amount emits ShowError`() = runTest {
        viewModel = createViewModel()

        viewModel.effect.test {
            viewModel.onEvent(BudgetsUiEvent.OnCreateBudgetClicked)
            val effect = awaitItem()
            assert(effect is BudgetsUiEffect.ShowError)
            assert((effect as BudgetsUiEffect.ShowError).message.contains("amount"))
        }
    }

    @Test
    fun `OnCreateBudgetClicked with no category emits ShowError`() = runTest {
        viewModel = createViewModel()
        viewModel.onEvent(BudgetsUiEvent.OnAmountDigitPressed("5"))

        // Clear the auto-selected category
        every { getCategories() } returns flowOf(emptyList())

        // Reset newBudgetState to have no category - override by testing null selectedCategory path
        // We recreate with empty categories so no auto-selection happens
        val vmNoCategory = BudgetsViewModel(
            getBudgetsByMonth, getMonthlyTransactions,
            mockk<GetCategoriesUseCase>().also {
                every { it() } returns flowOf(emptyList())
            },
            addBudget
        )
        vmNoCategory.onEvent(BudgetsUiEvent.OnAmountDigitPressed("5"))

        vmNoCategory.effect.test {
            vmNoCategory.onEvent(BudgetsUiEvent.OnCreateBudgetClicked)
            val effect = awaitItem()
            assert(effect is BudgetsUiEffect.ShowError)
            assert((effect as BudgetsUiEffect.ShowError).message.contains("category"))
        }
    }

    @Test
    fun `OnCreateBudgetClicked with valid data creates budget and resets state`() = runTest {
        coJustRun { addBudget(any()) }
        viewModel = createViewModel()

        viewModel.onEvent(BudgetsUiEvent.OnAmountDigitPressed("5"))
        viewModel.onEvent(BudgetsUiEvent.OnAmountDigitPressed("0"))
        viewModel.onEvent(BudgetsUiEvent.OnAmountDigitPressed("0"))

        viewModel.onEvent(BudgetsUiEvent.OnCreateBudgetClicked)

        assertEquals("0", viewModel.newBudgetState.value.amountInput)
        assertFalse(viewModel.state.value.showNewBudgetSheet)
        coVerify(exactly = 1) { addBudget(any()) }
    }

    @Test
    fun `budget loading populates budgets with spending data`() {
        val budget = Budget(1L, 1L, 500.0, YearMonth.now().monthValue, YearMonth.now().year)
        val expense = Transaction(1L, 200.0, TransactionType.EXPENSE, 1L, date = LocalDate.now())
        every { getBudgetsByMonth(any(), any()) } returns flowOf(listOf(budget))
        every { getMonthlyTransactions(any(), any()) } returns flowOf(listOf(expense))

        viewModel = createViewModel()

        val budgets = viewModel.state.value.budgets
        assertEquals(1, budgets.size)
        assertEquals(200.0, budgets[0].spent, 0.001)
        assertEquals(0.4f, budgets[0].percentage, 0.001f)
        assertFalse(budgets[0].isOverBudget)
    }

    @Test
    fun `budget isOverBudget is true when spent exceeds budget`() {
        val budget = Budget(1L, 1L, 100.0, YearMonth.now().monthValue, YearMonth.now().year)
        val expense = Transaction(1L, 150.0, TransactionType.EXPENSE, 1L, date = LocalDate.now())
        every { getBudgetsByMonth(any(), any()) } returns flowOf(listOf(budget))
        every { getMonthlyTransactions(any(), any()) } returns flowOf(listOf(expense))

        viewModel = createViewModel()

        assertTrue(viewModel.state.value.budgets[0].isOverBudget)
    }

    @Test
    fun `remainingBudget extension property computes correctly`() {
        val state = BudgetsUiState(totalBudget = 1000.0, totalSpent = 300.0)
        assertEquals(700.0, state.remainingBudget, 0.001)
    }

    @Test
    fun `overallPercentage extension property computes correctly`() {
        val state = BudgetsUiState(totalBudget = 500.0, totalSpent = 250.0)
        assertEquals(0.5f, state.overallPercentage, 0.001f)
    }

    @Test
    fun `overallPercentage is zero when no budget`() {
        val state = BudgetsUiState(totalBudget = 0.0, totalSpent = 0.0)
        assertEquals(0f, state.overallPercentage, 0.001f)
    }
}
