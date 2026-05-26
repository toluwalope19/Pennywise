package com.example.transactions.edit

import androidx.compose.runtime.Composable
import com.example.ui.components.CategoryPickerSheet
import com.example.ui.components.PennywiseDatePicker

@Composable
fun EditTransactionSheets(
    state: EditTransactionUiState,
    onEvent: (EditTransactionUiEvent) -> Unit
) {
    if (state.showCategoryPicker) {
        CategoryPickerSheet(
            categories = state.availableCategories,
            selectedCategoryId = state.selectedCategory?.id ?: 0L,
            onCategorySelected = { category ->
                onEvent(EditTransactionUiEvent.OnCategorySelected(category))
            },
            onDismiss = {
                onEvent(EditTransactionUiEvent.OnCategoryPickerDismiss)
            },
            onCategoryCreated = { _, _, _ -> }
        )
    }

    if (state.showDatePicker) {
        PennywiseDatePicker(
            selectedDate = state.selectedDate,
            onDateSelected = { date ->
                onEvent(EditTransactionUiEvent.OnDateSelected(date))
            },
            onDismiss = {
                onEvent(EditTransactionUiEvent.OnDatePickerDismiss)
            }
        )
    }

    if (state.showDeleteConfirm) {
        DeleteConfirmDialog(
            onConfirm = { onEvent(EditTransactionUiEvent.OnDeleteConfirmed) },
            onDismiss = { onEvent(EditTransactionUiEvent.OnDeleteDismissed) }
        )
    }
}