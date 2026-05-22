package com.example.analytics

import app.cash.turbine.test
import com.example.domain.model.Category
import com.example.domain.model.Transaction
import com.example.domain.model.TransactionType
import com.example.domain.usecase.category.GetCategoriesUseCase
import com.example.domain.usecase.transaction.GetTransactionsInRangeUseCase
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
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class AnalyticsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var getTransactionsInRange: GetTransactionsInRangeUseCase
    private lateinit var getCategories: GetCategoriesUseCase
    private lateinit var viewModel: AnalyticsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getTransactionsInRange = mockk()
        getCategories = mockk()

        every { getTransactionsInRange(any(), any()) } returns flowOf(emptyList())
        every { getCategories() } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = AnalyticsViewModel(getTransactionsInRange, getCategories)

    @Test
    fun `initial state sets correct selectedMonth and selectedYear`() {
        viewModel = createViewModel()
        val now = java.time.YearMonth.now()
        assertEquals(now.monthValue, viewModel.state.value.selectedMonth)
        assertEquals(now.year, viewModel.state.value.selectedYear)
    }

    @Test
    fun `LoadAnalytics with empty data sets isLoading false`() {
        viewModel = createViewModel()
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `LoadAnalytics with empty data has empty monthlyData`() {
        every { getTransactionsInRange(any(), any()) } returns flowOf(emptyList())
        every { getCategories() } returns flowOf(emptyList())

        viewModel = createViewModel()

        assertEquals(6, viewModel.state.value.monthlyData.size)
    }

    @Test
    fun `LoadAnalytics produces 6 months of data`() {
        viewModel = createViewModel()
        assertEquals(6, viewModel.state.value.monthlyData.size)
    }

    @Test
    fun `LoadAnalytics with empty transactions has zero totals`() {
        viewModel = createViewModel()
        assertEquals(0.0, viewModel.state.value.totalIncome6Months, 0.001)
        assertEquals(0.0, viewModel.state.value.totalExpense6Months, 0.001)
    }

    @Test
    fun `LoadAnalytics with no errors has null error`() {
        viewModel = createViewModel()
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `OnExportClicked emits ExportCSV effect`() = runTest {
        viewModel = createViewModel()

        viewModel.effect.test {
            viewModel.onEvent(AnalyticsUiEvent.OnExportClicked)
            assert(awaitItem() is AnalyticsUiEffect.ExportCSV)
        }
    }

    @Test
    fun `LoadAnalytics with income transactions updates totalIncome`() {
        val income = Transaction(
            id = 1L, amount = 1000.0, type = TransactionType.INCOME,
            categoryId = 1L, date = LocalDate.now()
        )
        every { getTransactionsInRange(any(), any()) } returns flowOf(listOf(income))
        every { getCategories() } returns flowOf(emptyList())

        viewModel = createViewModel()

        assert(viewModel.state.value.totalIncome6Months > 0)
    }

    @Test
    fun `LoadAnalytics with expense transactions updates totalExpense`() {
        val expense = Transaction(
            id = 1L, amount = 500.0, type = TransactionType.EXPENSE,
            categoryId = 1L, date = LocalDate.now()
        )
        every { getTransactionsInRange(any(), any()) } returns flowOf(listOf(expense))
        every { getCategories() } returns flowOf(emptyList())

        viewModel = createViewModel()

        assert(viewModel.state.value.totalExpense6Months > 0)
    }

    @Test
    fun `LoadAnalytics error sets error state`() {
        val error = RuntimeException("Network error")
        every { getTransactionsInRange(any(), any()) } returns kotlinx.coroutines.flow.flow {
            throw error
        }

        viewModel = createViewModel()

        assertEquals("Network error", viewModel.state.value.error)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `monthlyData months are in chronological order`() {
        viewModel = createViewModel()
        val months = viewModel.state.value.monthlyData.map { it.yearMonth }
        assertEquals(months.sorted(), months)
    }
}
