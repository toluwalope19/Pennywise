package com.example.transactions.add


import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.viewModelScope
import com.example.common.mvi.MviViewModel
import com.example.domain.model.Category
import com.example.domain.model.Transaction
import com.example.domain.model.TransactionType
import com.example.domain.usecase.category.AddCategoryUseCase
import com.example.domain.usecase.category.GetCategoriesUseCase
import com.example.domain.usecase.transaction.AddTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val addTransaction: AddTransactionUseCase,
    private val getCategories: GetCategoriesUseCase,
    private val addCategory: AddCategoryUseCase
) : MviViewModel<AddTransactionUiState, AddTransactionUiEvent, AddTransactionUiEffect>(
    initialState = AddTransactionUiState()
) {

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            getCategories().collect { categories ->
                setState {
                    val autoSelect = when {
                        selectedCategory == null ->
                            categories.find { it.name.equals("Food", ignoreCase = true) }
                        pendingCategoryName != null ->
                            categories.find { it.name == pendingCategoryName }
                        else -> null
                    }
                    copy(
                        availableCategories = categories,
                        selectedCategory = autoSelect ?: selectedCategory,
                        pendingCategoryName = if (autoSelect != null && pendingCategoryName != null) null
                            else pendingCategoryName
                    )
                }
            }
        }
    }


    override fun handleEvent(event: AddTransactionUiEvent) {
        when (event) {
            // Type toggle
            is AddTransactionUiEvent.OnTypeChanged -> {
                val defaultName = if (event.type == TransactionType.INCOME) "Income" else "Food"
                val defaultCategory = currentState.availableCategories.find {
                    it.name.equals(defaultName, ignoreCase = true)
                }
                setState {
                    copy(
                        transactionType = event.type,
                        selectedCategory = defaultCategory ?: selectedCategory
                    )
                }
            }

            // Numpad input
            is AddTransactionUiEvent.OnDigitPressed -> handleDigitInput(event.digit)
            AddTransactionUiEvent.OnDeletePressed -> handleDelete()
            AddTransactionUiEvent.OnDecimalPressed -> handleDecimal()

            // Quick amounts
            is AddTransactionUiEvent.OnQuickAmountPressed -> setState {
                copy(amountInput = event.amount.toBigDecimal()
                    .stripTrailingZeros()
                    .toPlainString()
                )
            }

            // Category picker
            AddTransactionUiEvent.OnCategoryPickerOpen -> setState {
                copy(showCategoryPicker = true)
            }
            AddTransactionUiEvent.OnCategoryPickerDismiss -> setState {
                copy(showCategoryPicker = false)
            }
            is AddTransactionUiEvent.OnCategorySelected -> setState {
                copy(selectedCategory = event.category, showCategoryPicker = false)
            }
            is AddTransactionUiEvent.OnCreateCategory -> {
                viewModelScope.launch {
                    try {
                        val colorHex = String.format("#%06X", 0xFFFFFF and event.color.toArgb())
                        // ImageVector.name returns "Rounded.SportsEsports" or "Rounded/SportsEsports"
                        val iconName = event.icon.name
                            .substringAfterLast("/")
                            .substringAfterLast(".")

                        addCategory(
                            Category(
                                name = event.name,
                                icon = iconName,
                                color = colorHex,
                                isDefault = false
                            )
                        )
                        // Mark the name as pending; loadCategories() flow will find and select it
                        setState { copy(pendingCategoryName = event.name, showCategoryPicker = false) }
                    } catch (e: Exception) {
                        setEffect(AddTransactionUiEffect.ShowError(
                            e.message ?: "Failed to create category"
                        ))
                    }
                }
            }

            // Date picker
            AddTransactionUiEvent.OnDatePickerOpen -> setState {
                copy(showDatePicker = true)
            }
            AddTransactionUiEvent.OnDatePickerDismiss -> setState {
                copy(showDatePicker = false)
            }
            is AddTransactionUiEvent.OnDateSelected -> setState {
                copy(
                    selectedDate = event.date,
                    showDatePicker = false
                )
            }

            // Note
            is AddTransactionUiEvent.OnNoteChanged -> setState {
                copy(note = event.note)
            }

            // ML Kit
            is AddTransactionUiEvent.OnReceiptScanned -> processReceipt(event.imageBytes)

            // Save
            AddTransactionUiEvent.OnSaveClicked -> saveTransaction()

            // Back
            AddTransactionUiEvent.OnBackClicked -> setEffect(
                AddTransactionUiEffect.NavigateBack
            )
        }
    }

    // ── Amount input handlers ──────────────────────────────────────────────

    private fun handleDigitInput(digit: String) {
        val current = currentState.amountInput

        val new = when {
            // Replace leading zero unless decimal follows
            current == "0" -> digit
            // Limit to 2 decimal places
            current.contains(".") &&
                    current.substringAfter(".").length >= 2 -> current
            // Limit total length to prevent overflow
            current.replace(".", "").length >= 10 -> current
            else -> current + digit
        }

        setState { copy(amountInput = new) }
    }

    private fun handleDelete() {
        val current = currentState.amountInput
        val new = when {
            current.length <= 1 -> "0"
            else -> current.dropLast(1)
        }
        setState { copy(amountInput = new) }
    }

    private fun handleDecimal() {
        val current = currentState.amountInput
        if (!current.contains(".")) {
            setState { copy(amountInput = "$current.") }
        }
    }

    // ── ML Kit receipt processing ──────────────────────────────────────────

    private fun processReceipt(imageBytes: ByteArray) {
        viewModelScope.launch {
            setState { copy(isScanning = true, scanError = null) }
            try {
                // ML Kit processing happens in the UI layer
                // ViewModel receives the parsed result via OnReceiptScanned
                // Amount parsing from receipt text is done here
                setState { copy(isScanning = false) }
            } catch (e: Exception) {
                setState {
                    copy(isScanning = false, scanError = "Could not read receipt")
                }
            }
        }
    }

    // ── Save transaction ───────────────────────────────────────────────────

    private fun saveTransaction() {
        val state = currentState

        if (state.amount <= 0.0) {
            setEffect(AddTransactionUiEffect.ShowError("Please enter an amount"))
            return
        }

        viewModelScope.launch {
            setState { copy(isSaving = true) }
            try {
                val categoryId = state.selectedCategory?.id ?: 9L

                addTransaction(
                    Transaction(
                        amount = state.amount,
                        type = state.transactionType,
                        categoryId = categoryId,
                        note = state.note.ifBlank { null },
                        date = state.selectedDate
                    )
                )
                setState { copy(isSaving = false) }
                setEffect(AddTransactionUiEffect.TransactionSaved)
            } catch (e: Exception) {
                setState { copy(isSaving = false) }
                setEffect(AddTransactionUiEffect.ShowError(
                    e.message ?: "Failed to save transaction"
                ))
            }
        }
    }

}