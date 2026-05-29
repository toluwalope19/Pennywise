package com.example.transactions.add



import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.TransactionType
import com.example.transactions.add.components.AddTransactionFoldableLayout
import com.example.transactions.add.components.AddTransactionTabletLandscapeLayout

import com.example.transactions.add.components.FieldRows
import com.example.transactions.add.components.StickyCta
import com.example.transactions.add.components.TypeToggle
import com.example.ui.PennywiseWindowLayout
import com.example.ui.components.CategoryPickerSheet
import com.example.ui.components.HeroAmountCard
import com.example.ui.components.PennywiseDatePicker
import com.example.ui.theme.Accent
import com.example.ui.theme.Background
import com.example.ui.theme.Income
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.TextPrimary


@Composable
fun AddTransactionScreen(
    onNavigateBack: () -> Unit,
    onTransactionSaved: () -> Unit,
    windowLayout: PennywiseWindowLayout = PennywiseWindowLayout.PhonePortrait,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                AddTransactionUiEffect.NavigateBack -> onNavigateBack()
                AddTransactionUiEffect.TransactionSaved -> {
                    onTransactionSaved()
                }
                is AddTransactionUiEffect.ShowError -> {
                    // Snackbar — wire later
                }
            }
        }
    }

    AddTransactionContent(
        state = state,
        windowLayout,
        onEvent = viewModel::onEvent
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTransactionPhoneLayout(
    state: AddTransactionUiState,
    onEvent: (AddTransactionUiEvent) -> Unit
) {
    // Derived colors — change with transaction type
    val accentColor = if (state.transactionType == TransactionType.EXPENSE) {
        Accent
    } else {
        Income
    }

    val screenTitle = if (state.transactionType == TransactionType.EXPENSE) {
        "New transaction"
    } else {
        "New income"
    }

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
                        onClick = { onEvent(AddTransactionUiEvent.OnBackClicked) }
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
                        text = screenTitle,
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Scrollable content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1 — Type toggle
                TypeToggle(
                    selectedType = state.transactionType,
                    onTypeChanged = { type ->
                        onEvent(AddTransactionUiEvent.OnTypeChanged(type))
                    }
                )

                // 2 — Hero amount card
                HeroAmountCard(
                    amountInput = state.amountInput,
                    transactionType = state.transactionType,
                    onDigitPressed = { digit ->
                        onEvent(AddTransactionUiEvent.OnDigitPressed(digit))
                    },
                    onDeletePressed = {
                        onEvent(AddTransactionUiEvent.OnDeletePressed)
                    },
                    onDecimalPressed = {
                        onEvent(AddTransactionUiEvent.OnDecimalPressed)
                    },
                    onQuickAmountPressed = { amount ->
                        onEvent(AddTransactionUiEvent.OnQuickAmountPressed(amount))
                    }
                )

                // 3 — Field rows
                FieldRows(
                    state = state,
                    onEvent = onEvent
                )

                // Space for sticky CTA
                Spacer(modifier = Modifier.height(100.dp))
            }

            // 4 — Sticky CTA pinned to bottom
            StickyCta(
                transactionType = state.transactionType,
                isSaving = state.isSaving,
                onSaveClicked = { onEvent(AddTransactionUiEvent.OnSaveClicked) },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }

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

@Composable
private fun AddTransactionContent(
    state: AddTransactionUiState,
    windowLayout: PennywiseWindowLayout,
    onEvent: (AddTransactionUiEvent) -> Unit
) {
    when (windowLayout) {
        is PennywiseWindowLayout.TabletLandscape ->
            AddTransactionTabletLandscapeLayout(
                state = state,
                onEvent = onEvent
            )
        is PennywiseWindowLayout.Foldable ->
            AddTransactionFoldableLayout(
                state = state,
                foldingFeature = windowLayout.foldingFeature,
                onEvent = onEvent
            )
        else ->
            // existing phone layout — unchanged
            AddTransactionPhoneLayout(
                state = state,
                onEvent = onEvent
            )
    }
}