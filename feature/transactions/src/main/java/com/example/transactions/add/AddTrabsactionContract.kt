package com.example.transactions.add



import androidx.compose.ui.graphics.vector.ImageVector
import com.example.domain.model.Category
import com.example.domain.model.TransactionType
import java.time.LocalDate

// ── UI State ──────────────────────────────────────────────────────────────

data class AddTransactionUiState(
    val availableCategories: List<Category> = emptyList(),
    // Type toggle
    val transactionType: TransactionType = TransactionType.EXPENSE,

    // Amount — stored as string so we can handle digit-by-digit input
    val amountInput: String = "0",

    // Selected category — null briefly until categories load from DB
    val selectedCategory: Category? = null,

    // Name of a just-created category, used to auto-select it after the flow re-emits
    val pendingCategoryName: String? = null,

    // Date — defaults to today
    val selectedDate: LocalDate = LocalDate.now(),

    // Note
    val note: String = "",

    // ML Kit receipt scan
    val isScanning: Boolean = false,
    val scanError: String? = null,

    // Saving state
    val isSaving: Boolean = false,
    val saveError: String? = null,

    // Category picker visibility
    val showCategoryPicker: Boolean = false,

    // Date picker visibility
    val showDatePicker: Boolean = false
)

// Computed property — parse amount string to Double
val AddTransactionUiState.amount: Double
    get() = amountInput.toDoubleOrNull() ?: 0.0

// ── Events ────────────────────────────────────────────────────────────────

sealed class AddTransactionUiEvent {
    // Type toggle
    data class OnTypeChanged(val type: TransactionType) : AddTransactionUiEvent()

    // Numpad input — one digit or action at a time
    data class OnDigitPressed(val digit: String) : AddTransactionUiEvent()
    data object OnDeletePressed : AddTransactionUiEvent()
    data object OnDecimalPressed : AddTransactionUiEvent()

    // Quick amount chips
    data class OnQuickAmountPressed(val amount: Double) : AddTransactionUiEvent()

    // Category
    data object OnCategoryPickerOpen : AddTransactionUiEvent()
    data object OnCategoryPickerDismiss : AddTransactionUiEvent()
    data class OnCategorySelected(val category: Category) : AddTransactionUiEvent()
    data class OnCreateCategory(
        val name: String,
        val color: androidx.compose.ui.graphics.Color,
        val icon: ImageVector
    ) : AddTransactionUiEvent()

    // Date
    data object OnDatePickerOpen : AddTransactionUiEvent()
    data object OnDatePickerDismiss : AddTransactionUiEvent()
    data class OnDateSelected(val date: LocalDate) : AddTransactionUiEvent()

    // Note
    data class OnNoteChanged(val note: String) : AddTransactionUiEvent()

    // ML Kit
    data class OnReceiptScanned(val imageBytes: ByteArray) : AddTransactionUiEvent()

    // Save
    data object OnSaveClicked : AddTransactionUiEvent()

    // Navigation
    data object OnBackClicked : AddTransactionUiEvent()
}

// ── Effects ────────────────────────────────────────────────────────────────

sealed class AddTransactionUiEffect {
    data object NavigateBack : AddTransactionUiEffect()
    data object TransactionSaved : AddTransactionUiEffect()
    data class ShowError(val message: String) : AddTransactionUiEffect()
}