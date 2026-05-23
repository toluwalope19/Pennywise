package com.example.transactions.list

import androidx.paging.PagingData
import app.cash.turbine.test
import com.example.domain.model.Category
import com.example.domain.usecase.category.GetCategoriesUseCase
import com.example.domain.usecase.transaction.GetTransactionsUseCase
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var getTransactions: GetTransactionsUseCase
    private lateinit var getCategories: GetCategoriesUseCase
    private lateinit var viewModel: TransactionsViewModel

    private val sampleCategory = Category(1L, "Food", "restaurant", "#FF5733")

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getTransactions = mockk()
        getCategories = mockk()

        every { getCategories() } returns flowOf(listOf(sampleCategory))
        every { getTransactions.byMonth(any(), any()) } returns flowOf(PagingData.empty())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = TransactionsViewModel(getTransactions, getCategories)

    // ── Initial state ──────────────────────────────────────────────────────

    @Test
    fun `initial selectedMonth is current month`() {
        viewModel = createViewModel()
        assertEquals(LocalDate.now().monthValue, viewModel.state.value.selectedMonth)
    }

    @Test
    fun `initial selectedYear is current year`() {
        viewModel = createViewModel()
        assertEquals(LocalDate.now().year, viewModel.state.value.selectedYear)
    }

    @Test
    fun `initial selectedFilter is ALL`() {
        viewModel = createViewModel()
        assertEquals(TransactionFilter.ALL, viewModel.state.value.selectedFilter)
    }

    @Test
    fun `init loads categories into categoryMap`() {
        viewModel = createViewModel()
        assertEquals(mapOf(1L to sampleCategory), viewModel.state.value.categoryMap)
    }

    // ── Filter ────────────────────────────────────────────────────────────

    @Test
    fun `OnFilterChanged updates selectedFilter to INCOME`() {
        viewModel = createViewModel()
        viewModel.onEvent(TransactionsUiEvent.OnFilterChanged(TransactionFilter.INCOME))
        assertEquals(TransactionFilter.INCOME, viewModel.state.value.selectedFilter)
    }

    @Test
    fun `OnFilterChanged updates selectedFilter to EXPENSE`() {
        viewModel = createViewModel()
        viewModel.onEvent(TransactionsUiEvent.OnFilterChanged(TransactionFilter.EXPENSE))
        assertEquals(TransactionFilter.EXPENSE, viewModel.state.value.selectedFilter)
    }

    @Test
    fun `OnFilterChanged back to ALL updates selectedFilter`() {
        viewModel = createViewModel()
        viewModel.onEvent(TransactionsUiEvent.OnFilterChanged(TransactionFilter.INCOME))
        viewModel.onEvent(TransactionsUiEvent.OnFilterChanged(TransactionFilter.ALL))
        assertEquals(TransactionFilter.ALL, viewModel.state.value.selectedFilter)
    }

    // ── Month navigation ───────────────────────────────────────────────────

    @Test
    fun `OnMonthChanged increments month`() {
        viewModel = createViewModel()
        val expected = YearMonth.now().plusMonths(1)

        viewModel.onEvent(TransactionsUiEvent.OnMonthChanged(1))

        assertEquals(expected.monthValue, viewModel.state.value.selectedMonth)
        assertEquals(expected.year, viewModel.state.value.selectedYear)
    }

    @Test
    fun `OnMonthChanged decrements month`() {
        viewModel = createViewModel()
        val expected = YearMonth.now().minusMonths(1)

        viewModel.onEvent(TransactionsUiEvent.OnMonthChanged(-1))

        assertEquals(expected.monthValue, viewModel.state.value.selectedMonth)
        assertEquals(expected.year, viewModel.state.value.selectedYear)
    }

    @Test
    fun `OnMonthChanged wraps from January back to December`() {
        // Start from January by navigating to it
        viewModel = createViewModel()
        val jan = YearMonth.of(YearMonth.now().year, 1)
        val monthsToJan = -(YearMonth.now().monthValue - 1)
        if (monthsToJan != 0) {
            viewModel.onEvent(TransactionsUiEvent.OnMonthChanged(monthsToJan))
        }

        viewModel.onEvent(TransactionsUiEvent.OnMonthChanged(-1))

        assertEquals(12, viewModel.state.value.selectedMonth)
    }

    // ── Effects ────────────────────────────────────────────────────────────

    @Test
    fun `OnTransactionClick emits NavigateToTransaction with correct id`() = runTest {
        viewModel = createViewModel()

        viewModel.effect.test {
            viewModel.onEvent(TransactionsUiEvent.OnTransactionClick(77L))
            val effect = awaitItem()
            assert(effect is TransactionsUiEffect.NavigateToTransaction)
            assertEquals(77L, (effect as TransactionsUiEffect.NavigateToTransaction).id)
        }
    }

    @Test
    fun `OnAddTransactionClick emits NavigateToAddTransaction`() = runTest {
        viewModel = createViewModel()

        viewModel.effect.test {
            viewModel.onEvent(TransactionsUiEvent.OnAddTransactionClick)
            assert(awaitItem() is TransactionsUiEffect.NavigateToAddTransaction)
        }
    }

    @Test
    fun `OnBackClick emits NavigateBack`() = runTest {
        viewModel = createViewModel()

        viewModel.effect.test {
            viewModel.onEvent(TransactionsUiEvent.OnBackClick)
            assert(awaitItem() is TransactionsUiEffect.NavigateBack)
        }
    }

    @Test
    fun `LoadTransactions event is a no-op and does not crash`() {
        viewModel = createViewModel()
        viewModel.onEvent(TransactionsUiEvent.LoadTransactions)
        // State should remain unchanged
        assertEquals(TransactionFilter.ALL, viewModel.state.value.selectedFilter)
    }

    // ── CategoryMap ───────────────────────────────────────────────────────

    @Test
    fun `categoryMap updates when categories emit new values`() {
        val updatedCategory = Category(1L, "Food Updated", "restaurant", "#FF5733")
        every { getCategories() } returns flowOf(listOf(updatedCategory))

        viewModel = createViewModel()

        assertEquals("Food Updated", viewModel.state.value.categoryMap[1L]?.name)
    }

    @Test
    fun `categoryMap is empty when no categories`() {
        every { getCategories() } returns flowOf(emptyList())

        viewModel = createViewModel()

        assertTrue(viewModel.state.value.categoryMap.isEmpty())
    }
}

// ── TransactionFilter extension ────────────────────────────────────────────

class TransactionFilterTest {

    @Test
    fun `ALL displayName is All`() {
        assertEquals("All", TransactionFilter.ALL.displayName())
    }

    @Test
    fun `INCOME displayName is Income`() {
        assertEquals("Income", TransactionFilter.INCOME.displayName())
    }

    @Test
    fun `EXPENSE displayName is Expense`() {
        assertEquals("Expense", TransactionFilter.EXPENSE.displayName())
    }
}
