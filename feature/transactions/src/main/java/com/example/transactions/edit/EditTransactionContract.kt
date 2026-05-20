package com.example.transactions.edit




import com.example.domain.model.Category
import com.example.domain.model.TransactionType
import java.time.LocalDate

data class EditTransactionUiState(
    val isLoading: Boolean = true,
    val transactionId: Long = 0L,
    val transactionType: TransactionType = TransactionType.EXPENSE,
    val amountInput: String = "0",
    val selectedCategory: Category? = null,
    val selectedDate: LocalDate = LocalDate.now(),
    val note: String = "",
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val showCategoryPicker: Boolean = false,
    val showDatePicker: Boolean = false,
    val showDeleteConfirm: Boolean = false,
    val availableCategories: List<Category> = emptyList(),
    val error: String? = null
)

val EditTransactionUiState.amount: Double
    get() = amountInput.toDoubleOrNull() ?: 0.0

sealed class EditTransactionUiEvent {
    data class OnTypeChanged(val type: TransactionType) : EditTransactionUiEvent()
    data class OnDigitPressed(val digit: String) : EditTransactionUiEvent()
    data object OnDeletePressed : EditTransactionUiEvent()
    data object OnDecimalPressed : EditTransactionUiEvent()
    data class OnQuickAmountPressed(val amount: Double) : EditTransactionUiEvent()
    data object OnCategoryPickerOpen : EditTransactionUiEvent()
    data object OnCategoryPickerDismiss : EditTransactionUiEvent()
    data class OnCategorySelected(val category: Category) : EditTransactionUiEvent()
    data object OnDatePickerOpen : EditTransactionUiEvent()
    data object OnDatePickerDismiss : EditTransactionUiEvent()
    data class OnDateSelected(val date: LocalDate) : EditTransactionUiEvent()
    data class OnNoteChanged(val note: String) : EditTransactionUiEvent()
    data object OnSaveClicked : EditTransactionUiEvent()
    data object OnDeleteClicked : EditTransactionUiEvent()
    data object OnDeleteConfirmed : EditTransactionUiEvent()
    data object OnDeleteDismissed : EditTransactionUiEvent()
    data object OnBackClicked : EditTransactionUiEvent()
}

sealed class EditTransactionUiEffect {
    data object NavigateBack : EditTransactionUiEffect()
    data object TransactionSaved : EditTransactionUiEffect()
    data object TransactionDeleted : EditTransactionUiEffect()
    data class ShowError(val message: String) : EditTransactionUiEffect()
}