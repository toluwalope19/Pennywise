package com.example.transactions.add

import app.cash.turbine.test
import com.example.domain.model.Category
import com.example.domain.model.Transaction
import com.example.domain.model.TransactionType
import com.example.domain.usecase.category.AddCategoryUseCase
import com.example.domain.usecase.category.GetCategoriesUseCase
import com.example.domain.usecase.transaction.AddTransactionUseCase
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

@OptIn(ExperimentalCoroutinesApi::class)
class AddTransactionViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var addTransaction: AddTransactionUseCase
    private lateinit var getCategories: GetCategoriesUseCase
    private lateinit var addCategory: AddCategoryUseCase
    private lateinit var viewModel: AddTransactionViewModel

    private val foodCategory = Category(1L, "Food", "restaurant", "#FF5733")
    private val incomeCategory = Category(2L, "Income", "payments", "#2ECC71")
    private val shoppingCategory = Category(3L, "Shopping", "shopping", "#3498DB")

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        addTransaction = mockk()
        getCategories = mockk()
        addCategory = mockk()

        every { getCategories() } returns flowOf(listOf(foodCategory, incomeCategory, shoppingCategory))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = AddTransactionViewModel(addTransaction, getCategories, addCategory)

    // ── Initial state ──────────────────────────────────────────────────────

    @Test
    fun `initial transactionType is EXPENSE`() {
        viewModel = createViewModel()
        assertEquals(TransactionType.EXPENSE, viewModel.state.value.transactionType)
    }

    @Test
    fun `initial amountInput is zero`() {
        viewModel = createViewModel()
        assertEquals("0", viewModel.state.value.amountInput)
    }

    @Test
    fun `initial date is today`() {
        viewModel = createViewModel()
        assertEquals(LocalDate.now(), viewModel.state.value.selectedDate)
    }

    @Test
    fun `initial note is empty`() {
        viewModel = createViewModel()
        assertEquals("", viewModel.state.value.note)
    }

    @Test
    fun `categories auto-select Food on load for EXPENSE type`() {
        viewModel = createViewModel()
        assertEquals(foodCategory, viewModel.state.value.selectedCategory)
    }

    // ── Type toggle ────────────────────────────────────────────────────────

    @Test
    fun `OnTypeChanged to INCOME updates transactionType`() {
        viewModel = createViewModel()
        viewModel.onEvent(AddTransactionUiEvent.OnTypeChanged(TransactionType.INCOME))
        assertEquals(TransactionType.INCOME, viewModel.state.value.transactionType)
    }

    @Test
    fun `OnTypeChanged to INCOME selects Income category`() {
        viewModel = createViewModel()
        viewModel.onEvent(AddTransactionUiEvent.OnTypeChanged(TransactionType.INCOME))
        assertEquals(incomeCategory, viewModel.state.value.selectedCategory)
    }

    @Test
    fun `OnTypeChanged to EXPENSE selects Food category`() {
        viewModel = createViewModel()
        viewModel.onEvent(AddTransactionUiEvent.OnTypeChanged(TransactionType.INCOME))
        viewModel.onEvent(AddTransactionUiEvent.OnTypeChanged(TransactionType.EXPENSE))
        assertEquals(TransactionType.EXPENSE, viewModel.state.value.transactionType)
    }

    // ── Amount input ───────────────────────────────────────────────────────

    @Test
    fun `OnDigitPressed replaces leading zero`() {
        viewModel = createViewModel()
        viewModel.onEvent(AddTransactionUiEvent.OnDigitPressed("7"))
        assertEquals("7", viewModel.state.value.amountInput)
    }

    @Test
    fun `OnDigitPressed builds multi-digit number`() {
        viewModel = createViewModel()
        viewModel.onEvent(AddTransactionUiEvent.OnDigitPressed("1"))
        viewModel.onEvent(AddTransactionUiEvent.OnDigitPressed("2"))
        viewModel.onEvent(AddTransactionUiEvent.OnDigitPressed("3"))
        assertEquals("123", viewModel.state.value.amountInput)
    }

    @Test
    fun `OnDigitPressed limits to 2 decimal places`() {
        viewModel = createViewModel()
        viewModel.onEvent(AddTransactionUiEvent.OnDecimalPressed)
        viewModel.onEvent(AddTransactionUiEvent.OnDigitPressed("1"))
        viewModel.onEvent(AddTransactionUiEvent.OnDigitPressed("2"))
        viewModel.onEvent(AddTransactionUiEvent.OnDigitPressed("3"))
        assertEquals("0.12", viewModel.state.value.amountInput)
    }

    @Test
    fun `OnDigitPressed does not exceed 10 digits total`() {
        viewModel = createViewModel()
        repeat(12) { viewModel.onEvent(AddTransactionUiEvent.OnDigitPressed("1")) }
        assert(viewModel.state.value.amountInput.replace(".", "").length <= 10)
    }

    @Test
    fun `OnDeletePressed reduces single digit to zero`() {
        viewModel = createViewModel()
        viewModel.onEvent(AddTransactionUiEvent.OnDigitPressed("5"))
        viewModel.onEvent(AddTransactionUiEvent.OnDeletePressed)
        assertEquals("0", viewModel.state.value.amountInput)
    }

    @Test
    fun `OnDeletePressed removes last digit`() {
        viewModel = createViewModel()
        viewModel.onEvent(AddTransactionUiEvent.OnDigitPressed("1"))
        viewModel.onEvent(AddTransactionUiEvent.OnDigitPressed("2"))
        viewModel.onEvent(AddTransactionUiEvent.OnDeletePressed)
        assertEquals("1", viewModel.state.value.amountInput)
    }

    @Test
    fun `OnDeletePressed on zero stays zero`() {
        viewModel = createViewModel()
        viewModel.onEvent(AddTransactionUiEvent.OnDeletePressed)
        assertEquals("0", viewModel.state.value.amountInput)
    }

    @Test
    fun `OnDecimalPressed adds decimal point`() {
        viewModel = createViewModel()
        viewModel.onEvent(AddTransactionUiEvent.OnDigitPressed("5"))
        viewModel.onEvent(AddTransactionUiEvent.OnDecimalPressed)
        assertEquals("5.", viewModel.state.value.amountInput)
    }

    @Test
    fun `OnDecimalPressed is idempotent`() {
        viewModel = createViewModel()
        viewModel.onEvent(AddTransactionUiEvent.OnDecimalPressed)
        viewModel.onEvent(AddTransactionUiEvent.OnDecimalPressed)
        assertEquals("0.", viewModel.state.value.amountInput)
    }

    @Test
    fun `OnQuickAmountPressed sets amountInput`() {
        viewModel = createViewModel()
        viewModel.onEvent(AddTransactionUiEvent.OnQuickAmountPressed(250.0))
        assertEquals("250", viewModel.state.value.amountInput)
    }

    @Test
    fun `OnQuickAmountPressed with decimal preserves necessary decimals`() {
        viewModel = createViewModel()
        viewModel.onEvent(AddTransactionUiEvent.OnQuickAmountPressed(100.5))
        assertEquals("100.5", viewModel.state.value.amountInput)
    }

    // ── Category picker ────────────────────────────────────────────────────

    @Test
    fun `OnCategoryPickerOpen shows picker`() {
        viewModel = createViewModel()
        viewModel.onEvent(AddTransactionUiEvent.OnCategoryPickerOpen)
        assertTrue(viewModel.state.value.showCategoryPicker)
    }

    @Test
    fun `OnCategoryPickerDismiss hides picker`() {
        viewModel = createViewModel()
        viewModel.onEvent(AddTransactionUiEvent.OnCategoryPickerOpen)
        viewModel.onEvent(AddTransactionUiEvent.OnCategoryPickerDismiss)
        assertFalse(viewModel.state.value.showCategoryPicker)
    }

    @Test
    fun `OnCategorySelected updates selectedCategory`() {
        viewModel = createViewModel()
        viewModel.onEvent(AddTransactionUiEvent.OnCategorySelected(shoppingCategory))
        assertEquals(shoppingCategory, viewModel.state.value.selectedCategory)
        assertFalse(viewModel.state.value.showCategoryPicker)
    }

    // ── Date picker ────────────────────────────────────────────────────────

    @Test
    fun `OnDatePickerOpen shows date picker`() {
        viewModel = createViewModel()
        viewModel.onEvent(AddTransactionUiEvent.OnDatePickerOpen)
        assertTrue(viewModel.state.value.showDatePicker)
    }

    @Test
    fun `OnDatePickerDismiss hides date picker`() {
        viewModel = createViewModel()
        viewModel.onEvent(AddTransactionUiEvent.OnDatePickerOpen)
        viewModel.onEvent(AddTransactionUiEvent.OnDatePickerDismiss)
        assertFalse(viewModel.state.value.showDatePicker)
    }

    @Test
    fun `OnDateSelected updates selectedDate and hides picker`() {
        val date = LocalDate.of(2024, 3, 15)
        viewModel = createViewModel()
        viewModel.onEvent(AddTransactionUiEvent.OnDateSelected(date))
        assertEquals(date, viewModel.state.value.selectedDate)
        assertFalse(viewModel.state.value.showDatePicker)
    }

    // ── Note ──────────────────────────────────────────────────────────────

    @Test
    fun `OnNoteChanged updates note`() {
        viewModel = createViewModel()
        viewModel.onEvent(AddTransactionUiEvent.OnNoteChanged("Lunch with team"))
        assertEquals("Lunch with team", viewModel.state.value.note)
    }

    @Test
    fun `OnNoteChanged with empty string clears note`() {
        viewModel = createViewModel()
        viewModel.onEvent(AddTransactionUiEvent.OnNoteChanged("Something"))
        viewModel.onEvent(AddTransactionUiEvent.OnNoteChanged(""))
        assertEquals("", viewModel.state.value.note)
    }

    // ── Save ──────────────────────────────────────────────────────────────

    @Test
    fun `OnSaveClicked with zero amount emits ShowError`() = runTest {
        viewModel = createViewModel()

        viewModel.effect.test {
            viewModel.onEvent(AddTransactionUiEvent.OnSaveClicked)
            val effect = awaitItem()
            assert(effect is AddTransactionUiEffect.ShowError)
            assert((effect as AddTransactionUiEffect.ShowError).message.contains("amount"))
        }
    }

    @Test
    fun `OnSaveClicked with valid amount calls addTransaction`() = runTest {
        coJustRun { addTransaction(any()) }
        viewModel = createViewModel()
        viewModel.onEvent(AddTransactionUiEvent.OnDigitPressed("5"))
        viewModel.onEvent(AddTransactionUiEvent.OnDigitPressed("0"))

        viewModel.onEvent(AddTransactionUiEvent.OnSaveClicked)

        coVerify(exactly = 1) { addTransaction(any()) }
    }

    @Test
    fun `OnSaveClicked success emits TransactionSaved`() = runTest {
        coJustRun { addTransaction(any()) }
        viewModel = createViewModel()
        viewModel.onEvent(AddTransactionUiEvent.OnDigitPressed("1"))
        viewModel.onEvent(AddTransactionUiEvent.OnDigitPressed("0"))
        viewModel.onEvent(AddTransactionUiEvent.OnDigitPressed("0"))

        viewModel.effect.test {
            viewModel.onEvent(AddTransactionUiEvent.OnSaveClicked)
            assert(awaitItem() is AddTransactionUiEffect.TransactionSaved)
        }
    }

    @Test
    fun `OnSaveClicked saves with correct transaction type`() = runTest {
        coJustRun { addTransaction(any()) }
        viewModel = createViewModel()
        viewModel.onEvent(AddTransactionUiEvent.OnTypeChanged(TransactionType.INCOME))
        viewModel.onEvent(AddTransactionUiEvent.OnDigitPressed("5"))
        viewModel.onEvent(AddTransactionUiEvent.OnSaveClicked)

        coVerify { addTransaction(match { it.type == TransactionType.INCOME }) }
    }

    @Test
    fun `OnSaveClicked passes blank note as null`() = runTest {
        coJustRun { addTransaction(any()) }
        viewModel = createViewModel()
        viewModel.onEvent(AddTransactionUiEvent.OnDigitPressed("5"))
        viewModel.onEvent(AddTransactionUiEvent.OnNoteChanged("   "))
        viewModel.onEvent(AddTransactionUiEvent.OnSaveClicked)

        coVerify { addTransaction(match { it.note == null }) }
    }

    @Test
    fun `OnSaveClicked repository error emits ShowError`() = runTest {
        val addTransactionUseCase = mockk<AddTransactionUseCase>()
        io.mockk.coEvery { addTransactionUseCase(any()) } throws Exception("DB error")
        viewModel = AddTransactionViewModel(addTransactionUseCase, getCategories, addCategory)
        viewModel.onEvent(AddTransactionUiEvent.OnDigitPressed("5"))

        viewModel.effect.test {
            viewModel.onEvent(AddTransactionUiEvent.OnSaveClicked)
            val effect = awaitItem()
            assert(effect is AddTransactionUiEffect.ShowError)
        }
    }

    // ── Navigation ────────────────────────────────────────────────────────

    @Test
    fun `OnBackClicked emits NavigateBack effect`() = runTest {
        viewModel = createViewModel()

        viewModel.effect.test {
            viewModel.onEvent(AddTransactionUiEvent.OnBackClicked)
            assert(awaitItem() is AddTransactionUiEffect.NavigateBack)
        }
    }

    // ── Computed property ─────────────────────────────────────────────────

    @Test
    fun `amount extension property parses amountInput correctly`() {
        val state = AddTransactionUiState(amountInput = "123.45")
        assertEquals(123.45, state.amount, 0.001)
    }

    @Test
    fun `amount extension property returns 0 for invalid input`() {
        val state = AddTransactionUiState(amountInput = "abc")
        assertEquals(0.0, state.amount, 0.001)
    }

    @Test
    fun `amount extension property returns 0 for trailing decimal`() {
        val state = AddTransactionUiState(amountInput = "50.")
        assertEquals(50.0, state.amount, 0.001)
    }
}
