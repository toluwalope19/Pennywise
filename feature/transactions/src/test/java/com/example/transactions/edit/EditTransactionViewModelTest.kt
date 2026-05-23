package com.example.transactions.edit

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.domain.model.Category
import com.example.domain.model.Transaction
import com.example.domain.model.TransactionType
import com.example.domain.usecase.category.GetCategoriesUseCase
import com.example.domain.usecase.transaction.DeleteTransactionUseCase
import com.example.domain.usecase.transaction.GetTransactionByIdUseCase
import com.example.domain.usecase.transaction.UpdateTransactionUseCase
import io.mockk.coEvery
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class EditTransactionViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var getTransactionById: GetTransactionByIdUseCase
    private lateinit var updateTransaction: UpdateTransactionUseCase
    private lateinit var deleteTransaction: DeleteTransactionUseCase
    private lateinit var getCategories: GetCategoriesUseCase
    private lateinit var viewModel: EditTransactionViewModel

    private val transactionId = 1L
    private val sampleCategory = Category(1L, "Food", "restaurant", "#FF5733")
    private val sampleTransaction = Transaction(
        id = transactionId,
        amount = 75.5,
        type = TransactionType.EXPENSE,
        categoryId = 1L,
        note = "Dinner",
        date = LocalDate.of(2024, 5, 10)
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        savedStateHandle = SavedStateHandle(mapOf("id" to transactionId))
        getTransactionById = mockk()
        updateTransaction = mockk()
        deleteTransaction = mockk()
        getCategories = mockk()

        coEvery { getTransactionById(transactionId) } returns sampleTransaction
        every { getCategories() } returns flowOf(listOf(sampleCategory))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = EditTransactionViewModel(
        savedStateHandle, getTransactionById, updateTransaction, deleteTransaction, getCategories
    )

    // ── Load transaction ───────────────────────────────────────────────────

    @Test
    fun `init loads transaction and populates state`() {
        viewModel = createViewModel()
        val state = viewModel.state.value
        assertEquals(transactionId, state.transactionId)
        assertEquals(TransactionType.EXPENSE, state.transactionType)
        assertEquals("75.5", state.amountInput)
        assertEquals(LocalDate.of(2024, 5, 10), state.selectedDate)
        assertEquals("Dinner", state.note)
    }

    @Test
    fun `init sets isLoading false after loading`() {
        viewModel = createViewModel()
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `init loads categories into availableCategories`() {
        viewModel = createViewModel()
        assertEquals(listOf(sampleCategory), viewModel.state.value.availableCategories)
    }

    @Test
    fun `transaction not found emits ShowError and NavigateBack`() = runTest {
        coEvery { getTransactionById(transactionId) } returns null

        viewModel = createViewModel()

        viewModel.effect.test {
            val first = awaitItem()
            assert(first is EditTransactionUiEffect.ShowError)

            val second = awaitItem()
            assert(second is EditTransactionUiEffect.NavigateBack)
        }
    }

    // ── Type toggle ────────────────────────────────────────────────────────

    @Test
    fun `OnTypeChanged updates transactionType`() {
        viewModel = createViewModel()
        viewModel.onEvent(EditTransactionUiEvent.OnTypeChanged(TransactionType.INCOME))
        assertEquals(TransactionType.INCOME, viewModel.state.value.transactionType)
    }

    // ── Amount input ───────────────────────────────────────────────────────

    @Test
    fun `OnDigitPressed replaces leading zero`() {
        viewModel = createViewModel()
        // Reset amount to "0" first
        viewModel.onEvent(EditTransactionUiEvent.OnQuickAmountPressed(0.0))
        viewModel.onEvent(EditTransactionUiEvent.OnDigitPressed("9"))
        assertEquals("9", viewModel.state.value.amountInput)
    }

    @Test
    fun `OnDigitPressed builds multi-digit amount`() {
        viewModel = createViewModel()
        viewModel.onEvent(EditTransactionUiEvent.OnQuickAmountPressed(0.0))
        viewModel.onEvent(EditTransactionUiEvent.OnDigitPressed("4"))
        viewModel.onEvent(EditTransactionUiEvent.OnDigitPressed("2"))
        assertEquals("42", viewModel.state.value.amountInput)
    }

    @Test
    fun `OnDigitPressed limits to 2 decimal places`() {
        viewModel = createViewModel()
        viewModel.onEvent(EditTransactionUiEvent.OnQuickAmountPressed(0.0))
        viewModel.onEvent(EditTransactionUiEvent.OnDecimalPressed)
        viewModel.onEvent(EditTransactionUiEvent.OnDigitPressed("1"))
        viewModel.onEvent(EditTransactionUiEvent.OnDigitPressed("2"))
        viewModel.onEvent(EditTransactionUiEvent.OnDigitPressed("3"))
        assertEquals("0.12", viewModel.state.value.amountInput)
    }

    @Test
    fun `OnDeletePressed removes last character`() {
        viewModel = createViewModel()
        viewModel.onEvent(EditTransactionUiEvent.OnDeletePressed)
        assertEquals("75.", viewModel.state.value.amountInput)
    }

    @Test
    fun `OnDeletePressed on single char resets to zero`() {
        viewModel = createViewModel()
        viewModel.onEvent(EditTransactionUiEvent.OnQuickAmountPressed(0.0))
        viewModel.onEvent(EditTransactionUiEvent.OnDigitPressed("5"))
        viewModel.onEvent(EditTransactionUiEvent.OnDeletePressed)
        assertEquals("0", viewModel.state.value.amountInput)
    }

    @Test
    fun `OnDecimalPressed adds decimal point when absent`() {
        viewModel = createViewModel()
        viewModel.onEvent(EditTransactionUiEvent.OnQuickAmountPressed(0.0))
        viewModel.onEvent(EditTransactionUiEvent.OnDigitPressed("5"))
        viewModel.onEvent(EditTransactionUiEvent.OnDecimalPressed)
        assertEquals("5.", viewModel.state.value.amountInput)
    }

    @Test
    fun `OnDecimalPressed is idempotent`() {
        viewModel = createViewModel()
        viewModel.onEvent(EditTransactionUiEvent.OnQuickAmountPressed(0.0))
        viewModel.onEvent(EditTransactionUiEvent.OnDecimalPressed)
        val after1 = viewModel.state.value.amountInput
        viewModel.onEvent(EditTransactionUiEvent.OnDecimalPressed)
        assertEquals(after1, viewModel.state.value.amountInput)
    }

    @Test
    fun `OnQuickAmountPressed sets amountInput`() {
        viewModel = createViewModel()
        viewModel.onEvent(EditTransactionUiEvent.OnQuickAmountPressed(500.0))
        assertEquals("500", viewModel.state.value.amountInput)
    }

    // ── Category picker ────────────────────────────────────────────────────

    @Test
    fun `OnCategoryPickerOpen shows picker`() {
        viewModel = createViewModel()
        viewModel.onEvent(EditTransactionUiEvent.OnCategoryPickerOpen)
        assertTrue(viewModel.state.value.showCategoryPicker)
    }

    @Test
    fun `OnCategoryPickerDismiss hides picker`() {
        viewModel = createViewModel()
        viewModel.onEvent(EditTransactionUiEvent.OnCategoryPickerOpen)
        viewModel.onEvent(EditTransactionUiEvent.OnCategoryPickerDismiss)
        assertFalse(viewModel.state.value.showCategoryPicker)
    }

    @Test
    fun `OnCategorySelected updates category and closes picker`() {
        val otherCategory = Category(2L, "Shopping", "shopping", "#3498DB")
        viewModel = createViewModel()
        viewModel.onEvent(EditTransactionUiEvent.OnCategorySelected(otherCategory))
        assertEquals(otherCategory, viewModel.state.value.selectedCategory)
        assertFalse(viewModel.state.value.showCategoryPicker)
    }

    // ── Date picker ────────────────────────────────────────────────────────

    @Test
    fun `OnDatePickerOpen shows date picker`() {
        viewModel = createViewModel()
        viewModel.onEvent(EditTransactionUiEvent.OnDatePickerOpen)
        assertTrue(viewModel.state.value.showDatePicker)
    }

    @Test
    fun `OnDatePickerDismiss hides date picker`() {
        viewModel = createViewModel()
        viewModel.onEvent(EditTransactionUiEvent.OnDatePickerOpen)
        viewModel.onEvent(EditTransactionUiEvent.OnDatePickerDismiss)
        assertFalse(viewModel.state.value.showDatePicker)
    }

    @Test
    fun `OnDateSelected updates selectedDate and hides picker`() {
        val newDate = LocalDate.of(2024, 8, 20)
        viewModel = createViewModel()
        viewModel.onEvent(EditTransactionUiEvent.OnDateSelected(newDate))
        assertEquals(newDate, viewModel.state.value.selectedDate)
        assertFalse(viewModel.state.value.showDatePicker)
    }

    // ── Note ──────────────────────────────────────────────────────────────

    @Test
    fun `OnNoteChanged updates note`() {
        viewModel = createViewModel()
        viewModel.onEvent(EditTransactionUiEvent.OnNoteChanged("Updated note"))
        assertEquals("Updated note", viewModel.state.value.note)
    }

    // ── Save ──────────────────────────────────────────────────────────────

    @Test
    fun `OnSaveClicked with zero amount emits ShowError`() = runTest {
        viewModel = createViewModel()
        viewModel.onEvent(EditTransactionUiEvent.OnQuickAmountPressed(0.0))

        viewModel.effect.test {
            viewModel.onEvent(EditTransactionUiEvent.OnSaveClicked)
            val effect = awaitItem()
            assert(effect is EditTransactionUiEffect.ShowError)
            assert((effect as EditTransactionUiEffect.ShowError).message.contains("amount"))
        }
    }

    @Test
    fun `OnSaveClicked with valid amount calls updateTransaction`() = runTest {
        coJustRun { updateTransaction(any()) }
        viewModel = createViewModel()

        viewModel.onEvent(EditTransactionUiEvent.OnSaveClicked)

        coVerify(exactly = 1) { updateTransaction(any()) }
    }

    @Test
    fun `OnSaveClicked success emits TransactionSaved`() = runTest {
        coJustRun { updateTransaction(any()) }
        viewModel = createViewModel()

        viewModel.effect.test {
            viewModel.onEvent(EditTransactionUiEvent.OnSaveClicked)
            assert(awaitItem() is EditTransactionUiEffect.TransactionSaved)
        }
    }

    @Test
    fun `OnSaveClicked passes blank note as null`() = runTest {
        coJustRun { updateTransaction(any()) }
        viewModel = createViewModel()
        viewModel.onEvent(EditTransactionUiEvent.OnNoteChanged("   "))
        viewModel.onEvent(EditTransactionUiEvent.OnSaveClicked)

        coVerify { updateTransaction(match { it.note == null }) }
    }

    @Test
    fun `OnSaveClicked repository error emits ShowError`() = runTest {
        coEvery { updateTransaction(any()) } throws Exception("Update failed")
        viewModel = createViewModel()

        viewModel.effect.test {
            viewModel.onEvent(EditTransactionUiEvent.OnSaveClicked)
            val effect = awaitItem()
            assert(effect is EditTransactionUiEffect.ShowError)
        }
    }

    @Test
    fun `OnSaveClicked resets isSaving to false on error`() = runTest {
        coEvery { updateTransaction(any()) } throws Exception("Failed")
        viewModel = createViewModel()

        viewModel.onEvent(EditTransactionUiEvent.OnSaveClicked)

        assertFalse(viewModel.state.value.isSaving)
    }

    // ── Delete ────────────────────────────────────────────────────────────

    @Test
    fun `OnDeleteClicked shows delete confirmation`() {
        viewModel = createViewModel()
        viewModel.onEvent(EditTransactionUiEvent.OnDeleteClicked)
        assertTrue(viewModel.state.value.showDeleteConfirm)
    }

    @Test
    fun `OnDeleteDismissed hides delete confirmation`() {
        viewModel = createViewModel()
        viewModel.onEvent(EditTransactionUiEvent.OnDeleteClicked)
        viewModel.onEvent(EditTransactionUiEvent.OnDeleteDismissed)
        assertFalse(viewModel.state.value.showDeleteConfirm)
    }

    @Test
    fun `OnDeleteConfirmed calls deleteTransaction`() = runTest {
        coJustRun { deleteTransaction(transactionId) }
        viewModel = createViewModel()

        viewModel.onEvent(EditTransactionUiEvent.OnDeleteConfirmed)

        coVerify(exactly = 1) { deleteTransaction(transactionId) }
    }

    @Test
    fun `OnDeleteConfirmed success emits TransactionDeleted`() = runTest {
        coJustRun { deleteTransaction(transactionId) }
        viewModel = createViewModel()

        viewModel.effect.test {
            viewModel.onEvent(EditTransactionUiEvent.OnDeleteConfirmed)
            assert(awaitItem() is EditTransactionUiEffect.TransactionDeleted)
        }
    }

    @Test
    fun `OnDeleteConfirmed failure emits ShowError`() = runTest {
        coEvery { deleteTransaction(transactionId) } throws Exception("Delete failed")
        viewModel = createViewModel()

        viewModel.effect.test {
            viewModel.onEvent(EditTransactionUiEvent.OnDeleteConfirmed)
            val effect = awaitItem()
            assert(effect is EditTransactionUiEffect.ShowError)
        }
    }

    @Test
    fun `OnDeleteConfirmed resets isDeleting to false on error`() = runTest {
        coEvery { deleteTransaction(transactionId) } throws Exception("Failed")
        viewModel = createViewModel()

        viewModel.onEvent(EditTransactionUiEvent.OnDeleteConfirmed)

        assertFalse(viewModel.state.value.isDeleting)
    }

    // ── Back navigation ───────────────────────────────────────────────────

    @Test
    fun `OnBackClicked emits NavigateBack effect`() = runTest {
        viewModel = createViewModel()

        viewModel.effect.test {
            viewModel.onEvent(EditTransactionUiEvent.OnBackClicked)
            assert(awaitItem() is EditTransactionUiEffect.NavigateBack)
        }
    }

    // ── Computed property ─────────────────────────────────────────────────

    @Test
    fun `amount extension property parses amountInput correctly`() {
        val state = EditTransactionUiState(amountInput = "99.99")
        assertEquals(99.99, state.amount, 0.001)
    }

    @Test
    fun `amount extension property returns zero for invalid input`() {
        val state = EditTransactionUiState(amountInput = "")
        assertEquals(0.0, state.amount, 0.001)
    }
}
