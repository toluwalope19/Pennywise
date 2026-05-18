package com.example.transactions.add



import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.TransactionType
import com.example.transactions.add.components.CategoryPickerSheet
import com.example.transactions.add.components.HeroAmountCard
import com.example.transactions.add.components.TypeToggle
import com.example.ui.theme.Accent
import com.example.ui.theme.Background
import com.example.ui.theme.Income
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.TextPrimary


@Composable
fun AddTransactionScreen(
    onNavigateBack: () -> Unit,
    onTransactionSaved: () -> Unit,
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
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTransactionContent(
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            TypeToggle(
                selectedType = state.transactionType,
                onTypeChanged = { type ->
                    onEvent(AddTransactionUiEvent.OnTypeChanged(type))
                }
            )

            HeroAmountCard(
                amountInput = state.amountInput,
                transactionType = state.transactionType,
                onQuickAmountPressed = { amount ->
                    onEvent(AddTransactionUiEvent.OnQuickAmountPressed(amount))
                }
            )
        }
    }

    if (state.showCategoryPicker) {
        CategoryPickerSheet(
            categories = state.availableCategories,
            selectedCategory = state.selectedCategory,
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
}