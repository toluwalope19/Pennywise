package com.example.dashboard

import app.cash.turbine.test
import com.example.domain.model.Category
import com.example.domain.model.Transaction
import com.example.domain.model.TransactionType
import com.example.domain.usecase.category.GetCategoriesUseCase
import com.example.domain.usecase.transaction.GetAllTimeBalanceUseCase
import com.example.domain.usecase.transaction.GetMonthlyTransactionsUseCase
import com.example.domain.usecase.transaction.GetRecentTransactionsUseCase
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
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var getRecentTransactions: GetRecentTransactionsUseCase
    private lateinit var getMonthlyTransactions: GetMonthlyTransactionsUseCase
    private lateinit var getCategories: GetCategoriesUseCase
    private lateinit var getAllTimeBalance: GetAllTimeBalanceUseCase
    private lateinit var viewModel: DashboardViewModel

    private val sampleCategory = Category(1L, "Food", "restaurant", "#FF5733")
    private val sampleTransaction = Transaction(
        id = 1L, amount = 50.0, type = TransactionType.EXPENSE,
        categoryId = 1L, date = LocalDate.now()
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getRecentTransactions = mockk()
        getMonthlyTransactions = mockk()
        getCategories = mockk()
        getAllTimeBalance = mockk()

        every { getRecentTransactions(5) } returns flowOf(listOf(sampleTransaction))
        every { getMonthlyTransactions(any(), any()) } returns flowOf(emptyList())
        every { getCategories() } returns flowOf(listOf(sampleCategory))
        every { getAllTimeBalance() } returns flowOf(500.0)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = DashboardViewModel(
        getRecentTransactions, getMonthlyTransactions, getCategories, getAllTimeBalance
    )

    @Test
    fun `initial state has current month and year`() {
        viewModel = createViewModel()
        val state = viewModel.state.value
        assertEquals(LocalDate.now().monthValue, state.selectedMonth)
        assertEquals(LocalDate.now().year, state.selectedYear)
    }

    @Test
    fun `LoadDashboard sets isLoading false after data loads`() {
        viewModel = createViewModel()
        assertEquals(false, viewModel.state.value.isLoading)
    }

    @Test
    fun `LoadDashboard updates totalBalance from flow`() {
        viewModel = createViewModel()
        assertEquals(500.0, viewModel.state.value.totalBalance, 0.001)
    }

    @Test
    fun `LoadDashboard populates recentTransactions`() {
        viewModel = createViewModel()
        assertEquals(listOf(sampleTransaction), viewModel.state.value.recentTransactions)
    }

    @Test
    fun `LoadDashboard populates categoryMap`() {
        viewModel = createViewModel()
        assertEquals(mapOf(1L to sampleCategory), viewModel.state.value.categoryMap)
    }

    @Test
    fun `OnMonthChanged increments month`() {
        viewModel = createViewModel()
        val initialMonth = viewModel.state.value.selectedMonth

        every { getMonthlyTransactions(any(), any()) } returns flowOf(emptyList())
        viewModel.onEvent(DashboardUiEvent.OnMonthChanged(1))

        val expected = java.time.YearMonth.now().plusMonths(1)
        assertEquals(expected.monthValue, viewModel.state.value.selectedMonth)
        assertEquals(expected.year, viewModel.state.value.selectedYear)
    }

    @Test
    fun `OnMonthChanged decrements month`() {
        viewModel = createViewModel()

        every { getMonthlyTransactions(any(), any()) } returns flowOf(emptyList())
        viewModel.onEvent(DashboardUiEvent.OnMonthChanged(-1))

        val expected = java.time.YearMonth.now().minusMonths(1)
        assertEquals(expected.monthValue, viewModel.state.value.selectedMonth)
        assertEquals(expected.year, viewModel.state.value.selectedYear)
    }

    @Test
    fun `OnTransactionClick emits NavigateToTransaction effect`() = runTest {
        viewModel = createViewModel()

        viewModel.effect.test {
            viewModel.onEvent(DashboardUiEvent.OnTransactionClick(42L))
            val effect = awaitItem()
            assert(effect is DashboardUiEffect.NavigateToTransaction)
            assertEquals(42L, (effect as DashboardUiEffect.NavigateToTransaction).id)
        }
    }

    @Test
    fun `OnSeeAllTransactionsClick emits NavigateToTransactions effect`() = runTest {
        viewModel = createViewModel()

        viewModel.effect.test {
            viewModel.onEvent(DashboardUiEvent.OnSeeAllTransactionsClick)
            assert(awaitItem() is DashboardUiEffect.NavigateToTransactions)
        }
    }

    @Test
    fun `OnAddTransactionClick emits NavigateToAddTransaction effect`() = runTest {
        viewModel = createViewModel()

        viewModel.effect.test {
            viewModel.onEvent(DashboardUiEvent.OnAddTransactionClick)
            assert(awaitItem() is DashboardUiEffect.NavigateToAddTransaction)
        }
    }

    @Test
    fun `OnSettingsClick emits NavigateToSettings effect`() = runTest {
        viewModel = createViewModel()

        viewModel.effect.test {
            viewModel.onEvent(DashboardUiEvent.OnSettingsClick)
            assert(awaitItem() is DashboardUiEffect.NavigateToSettings)
        }
    }

    @Test
    fun `dashboard computes totalIncome and totalExpense correctly`() {
        val income = Transaction(2L, 1000.0, TransactionType.INCOME, 1L, date = LocalDate.now())
        val expense = Transaction(3L, 300.0, TransactionType.EXPENSE, 1L, date = LocalDate.now())
        every { getMonthlyTransactions(any(), any()) } returns flowOf(listOf(income, expense))

        viewModel = createViewModel()

        assertEquals(1000.0, viewModel.state.value.totalIncome, 0.001)
        assertEquals(300.0, viewModel.state.value.totalExpense, 0.001)
    }
}
