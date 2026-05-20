package com.example.transactions.edit



import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.common.mvi.MviViewModel
import com.example.domain.model.Transaction
import com.example.domain.usecase.category.GetCategoriesUseCase
import com.example.domain.usecase.transaction.DeleteTransactionUseCase
import com.example.domain.usecase.transaction.GetTransactionByIdUseCase
import com.example.domain.usecase.transaction.UpdateTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditTransactionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTransactionById: GetTransactionByIdUseCase,
    private val updateTransaction: UpdateTransactionUseCase,
    private val deleteTransaction: DeleteTransactionUseCase,
    private val getCategories: GetCategoriesUseCase
) : MviViewModel<EditTransactionUiState, EditTransactionUiEvent, EditTransactionUiEffect>(
    initialState = EditTransactionUiState()
) {
    // Get transaction ID from nav argument
    private val transactionId: Long =
        checkNotNull(savedStateHandle["id"])

    init {
        loadTransaction()
        loadCategories()
    }

    private fun loadTransaction() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val transaction = getTransactionById(transactionId)
            if (transaction == null) {
                setEffect(EditTransactionUiEffect.ShowError("Transaction not found"))
                setEffect(EditTransactionUiEffect.NavigateBack)
                return@launch
            }
            setState {
                copy(
                    isLoading = false,
                    transactionId = transaction.id,
                    transactionType = transaction.type,
                    amountInput = transaction.amount.toBigDecimal()
                        .stripTrailingZeros()
                        .toPlainString(),
                    selectedDate = transaction.date,
                    note = transaction.note ?: ""
                )
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            getCategories().collect { categories ->
                setState {
                    val selected = categories.firstOrNull {
                        it.id == currentState.selectedCategory?.id
                    } ?: categories.firstOrNull()
                    copy(
                        availableCategories = categories,
                        selectedCategory = selected
                    )
                }
            }
        }
    }

    override fun handleEvent(event: EditTransactionUiEvent) {
        when (event) {
            is EditTransactionUiEvent.OnTypeChanged -> setState {
                copy(transactionType = event.type)
            }
            is EditTransactionUiEvent.OnDigitPressed -> handleDigitInput(event.digit)
            EditTransactionUiEvent.OnDeletePressed -> handleDelete()
            EditTransactionUiEvent.OnDecimalPressed -> handleDecimal()
            is EditTransactionUiEvent.OnQuickAmountPressed -> setState {
                copy(amountInput = event.amount.toBigDecimal()
                    .stripTrailingZeros()
                    .toPlainString()
                )
            }
            EditTransactionUiEvent.OnCategoryPickerOpen -> setState {
                copy(showCategoryPicker = true)
            }
            EditTransactionUiEvent.OnCategoryPickerDismiss -> setState {
                copy(showCategoryPicker = false)
            }
            is EditTransactionUiEvent.OnCategorySelected -> setState {
                copy(selectedCategory = event.category, showCategoryPicker = false)
            }
            EditTransactionUiEvent.OnDatePickerOpen -> setState {
                copy(showDatePicker = true)
            }
            EditTransactionUiEvent.OnDatePickerDismiss -> setState {
                copy(showDatePicker = false)
            }
            is EditTransactionUiEvent.OnDateSelected -> setState {
                copy(selectedDate = event.date, showDatePicker = false)
            }
            is EditTransactionUiEvent.OnNoteChanged -> setState {
                copy(note = event.note)
            }
            EditTransactionUiEvent.OnSaveClicked -> saveTransaction()
            EditTransactionUiEvent.OnDeleteClicked -> setState {
                copy(showDeleteConfirm = true)
            }
            EditTransactionUiEvent.OnDeleteConfirmed -> deleteTransactionById()
            EditTransactionUiEvent.OnDeleteDismissed -> setState {
                copy(showDeleteConfirm = false)
            }
            EditTransactionUiEvent.OnBackClicked -> setEffect(
                EditTransactionUiEffect.NavigateBack
            )
        }
    }

    private fun handleDigitInput(digit: String) {
        val current = currentState.amountInput
        val new = when {
            current == "0" -> digit
            current.contains(".") &&
                    current.substringAfter(".").length >= 2 -> current
            current.replace(".", "").length >= 10 -> current
            else -> current + digit
        }
        setState { copy(amountInput = new) }
    }

    private fun handleDelete() {
        val current = currentState.amountInput
        setState {
            copy(amountInput = if (current.length <= 1) "0" else current.dropLast(1))
        }
    }

    private fun handleDecimal() {
        val current = currentState.amountInput
        if (!current.contains(".")) {
            setState { copy(amountInput = "$current.") }
        }
    }

    private fun saveTransaction() {
        val state = currentState
        if (state.amount <= 0.0) {
            setEffect(EditTransactionUiEffect.ShowError("Please enter an amount"))
            return
        }
        viewModelScope.launch {
            setState { copy(isSaving = true) }
            try {
                updateTransaction(
                    Transaction(
                        id = state.transactionId,
                        amount = state.amount,
                        type = state.transactionType,
                        categoryId = state.selectedCategory?.id ?: 1L,
                        note = state.note.ifBlank { null },
                        date = state.selectedDate
                    )
                )
                setState { copy(isSaving = false) }
                setEffect(EditTransactionUiEffect.TransactionSaved)
            } catch (e: Exception) {
                setState { copy(isSaving = false) }
                setEffect(EditTransactionUiEffect.ShowError(
                    e.message ?: "Failed to update transaction"
                ))
            }
        }
    }

    private fun deleteTransactionById() {
        viewModelScope.launch {
            setState { copy(isDeleting = true) }
            try {
                deleteTransaction(transactionId)
                setState { copy(isDeleting = false) }
                setEffect(EditTransactionUiEffect.TransactionDeleted)
            } catch (e: Exception) {
                setState { copy(isDeleting = false) }
                setEffect(EditTransactionUiEffect.ShowError(
                    e.message ?: "Failed to delete transaction"
                ))
            }
        }
    }
}