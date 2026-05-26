package com.example.transactions.add.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.TransactionType
import com.example.transactions.add.AddTransactionUiEvent
import com.example.transactions.add.AddTransactionUiState
import com.example.ui.components.CategoryPickerSheet
import com.example.ui.components.HeroAmountCard
import com.example.ui.components.PennywiseDatePicker
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.Surface
import com.example.ui.theme.TextPrimary

@Composable
fun AddTransactionSheets(
    state: AddTransactionUiState,
    onEvent: (AddTransactionUiEvent) -> Unit
) {
    if (state.showCategoryPicker) {
        CategoryPickerSheet(
            categories = state.availableCategories,
            selectedCategoryId = state.selectedCategory?.id ?: 0L,
            onCategorySelected = { category ->
                onEvent(AddTransactionUiEvent.OnCategorySelected(category))
            },
            onDismiss = {
                onEvent(AddTransactionUiEvent.OnCategoryPickerDismiss)
            },
            onCategoryCreated = { name, color, icon ->
                onEvent(AddTransactionUiEvent.OnCreateCategory(name, color, icon))
            }
        )
    }

    if (state.showDatePicker) {
        PennywiseDatePicker(
            selectedDate = state.selectedDate,
            onDateSelected = { date ->
                onEvent(AddTransactionUiEvent.OnDateSelected(date))
            },
            onDismiss = {
                onEvent(AddTransactionUiEvent.OnDatePickerDismiss)
            }
        )
    }
}