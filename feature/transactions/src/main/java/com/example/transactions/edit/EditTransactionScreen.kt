package com.example.transactions.edit


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.transactions.add.AddTransactionUiEvent
import com.example.transactions.add.AddTransactionUiState
import com.example.transactions.add.components.CategoryPickerSheet
import com.example.transactions.add.components.FieldRows
import com.example.transactions.add.components.HeroAmountCard
import com.example.transactions.add.components.StickyCta
import com.example.transactions.add.components.TypeToggle
import com.example.ui.components.CategoryType
import com.example.ui.components.PennywiseDatePicker
import com.example.ui.theme.Accent
import com.example.ui.theme.Background
import com.example.ui.theme.Expense
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.TextPrimary

@Composable
fun EditTransactionScreen(
    onNavigateBack: () -> Unit,
    viewModel: EditTransactionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                EditTransactionUiEffect.NavigateBack -> onNavigateBack()
                EditTransactionUiEffect.TransactionSaved -> onNavigateBack()
                EditTransactionUiEffect.TransactionDeleted -> onNavigateBack()
                is EditTransactionUiEffect.ShowError -> { /* Snackbar later */ }
            }
        }
    }

    EditTransactionContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditTransactionContent(
    state: EditTransactionUiState,
    onEvent: (EditTransactionUiEvent) -> Unit
) {
    Scaffold(
        containerColor = Background,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = WindowInsets(0),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Background
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { onEvent(EditTransactionUiEvent.OnBackClicked) }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                title = {
                    Text(
                        text = "Edit transaction",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                },
                actions = {
                    // Delete button — top right
                    IconButton(
                        onClick = { onEvent(EditTransactionUiEvent.OnDeleteClicked) }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.DeleteOutline,
                            contentDescription = "Delete transaction",
                            tint = Expense,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Accent)
            }
            return@Scaffold
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Type toggle
                TypeToggle(
                    selectedType = state.transactionType,
                    onTypeChanged = { type ->
                        onEvent(EditTransactionUiEvent.OnTypeChanged(type))
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Hero amount card
                HeroAmountCard(
                    amountInput = state.amountInput,
                    transactionType = state.transactionType,
                    onDigitPressed = { digit ->
                        onEvent(EditTransactionUiEvent.OnDigitPressed(digit))
                    },
                    onDeletePressed = {
                        onEvent(EditTransactionUiEvent.OnDeletePressed)
                    },
                    onDecimalPressed = {
                        onEvent(EditTransactionUiEvent.OnDecimalPressed)
                    },
                    onQuickAmountPressed = { amount ->
                        onEvent(EditTransactionUiEvent.OnQuickAmountPressed(amount))
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Field rows — category, date, receipt, note
                FieldRows(
                    state = state.toAddState(),
                    onEvent = { addEvent -> onEvent(addEvent.toEditEvent()) }
                )

                Spacer(modifier = Modifier.height(100.dp))
            }

            // Sticky CTA — Save
            StickyCta(
                transactionType = state.transactionType,
                isSaving = state.isSaving,
                onSaveClicked = { onEvent(EditTransactionUiEvent.OnSaveClicked) },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }

    // Category picker
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
            onCategoryCreated = { name, color, icon ->
                // Wire to ViewModel if needed
            }
        )
    }

    // Date picker
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

    // Delete confirmation dialog
    if (state.showDeleteConfirm) {
        DeleteConfirmDialog(
            onConfirm = { onEvent(EditTransactionUiEvent.OnDeleteConfirmed) },
            onDismiss = { onEvent(EditTransactionUiEvent.OnDeleteDismissed) }
        )
    }
}

// ── Delete confirmation dialog ─────────────────────────────────────────────

@Composable
private fun DeleteConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E1E1E),
        title = {
            Text(
                text = "Delete transaction",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = TextPrimary
            )
        },
        text = {
            Text(
                text = "This transaction will be permanently deleted. This action cannot be undone.",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = Color(0xFF888888)
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "Delete",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = Expense
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF888888)
                )
            }
        }
    )
}

// Maps EditTransactionUiState → AddTransactionUiState for reusing FieldRows
private fun EditTransactionUiState.toAddState() =
    AddTransactionUiState(
        transactionType = transactionType,
        amountInput = amountInput,
        selectedCategory = selectedCategory,
        selectedDate = selectedDate,
        note = note,
        isScanning = false,
        showCategoryPicker = showCategoryPicker,
        showDatePicker = showDatePicker,
        availableCategories = availableCategories
    )

// Maps AddTransactionUiEvent → EditTransactionUiEvent
private fun AddTransactionUiEvent.toEditEvent():
        EditTransactionUiEvent = when (this) {
    is AddTransactionUiEvent.OnCategoryPickerOpen ->
        EditTransactionUiEvent.OnCategoryPickerOpen
    is AddTransactionUiEvent.OnCategoryPickerDismiss ->
        EditTransactionUiEvent.OnCategoryPickerDismiss
    is AddTransactionUiEvent.OnCategorySelected ->
        EditTransactionUiEvent.OnCategorySelected(category)
    is AddTransactionUiEvent.OnDatePickerOpen ->
        EditTransactionUiEvent.OnDatePickerOpen
    is AddTransactionUiEvent.OnDatePickerDismiss ->
        EditTransactionUiEvent.OnDatePickerDismiss
    is AddTransactionUiEvent.OnNoteChanged ->
        EditTransactionUiEvent.OnNoteChanged(note)
    else -> EditTransactionUiEvent.OnBackClicked
}