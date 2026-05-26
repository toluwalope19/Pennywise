package com.example.transactions.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.example.domain.model.Category
import com.example.transactions.add.components.FieldRows
import com.example.transactions.add.components.TypeToggle
import com.example.transactions.edit.DeleteConfirmDialog
import com.example.transactions.edit.EditTransactionUiEffect
import com.example.transactions.edit.EditTransactionUiEvent
import com.example.transactions.edit.EditTransactionViewModel
import com.example.transactions.edit.toAddState
import com.example.transactions.edit.toEditEvent
import com.example.transactions.list.TransactionListItem
import com.example.transactions.list.TransactionsUiEvent
import com.example.transactions.list.TransactionsUiState
import com.example.ui.components.CategoryDisplay
import com.example.ui.components.CategoryPickerSheet
import com.example.ui.components.HeroAmountCard
import com.example.ui.components.PennywiseDatePicker
import com.example.ui.components.toDisplay
import com.example.ui.theme.Accent
import com.example.ui.theme.Background
import com.example.ui.theme.Expense
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MasterDetailEditPanel(
    transactionId: Long,
    onDeleted: () -> Unit,
) {

    val viewModel = hiltViewModel<EditTransactionViewModel,
            EditTransactionViewModel.Factory> { factory ->
        factory.create(transactionId)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                EditTransactionUiEffect.TransactionDeleted -> onDeleted()
                EditTransactionUiEffect.NavigateBack -> onDeleted()
                else -> {}
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // TopBar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MasterDetailIconButton(
                    icon = Icons.AutoMirrored.Rounded.ArrowBack,
                    onClick = onDeleted
                )
                Text(
                    text = "Edit transaction",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
                MasterDetailIconButton(
                    icon = Icons.Rounded.Delete,
                    onClick = {
                        viewModel.onEvent(EditTransactionUiEvent.OnDeleteClicked)
                    },
                    tint = Expense
                )
            }

            // Scrollable content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (state.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Accent)
                    }
                } else {
                    TypeToggle(
                        selectedType = state.transactionType,
                        onTypeChanged = { type ->
                            viewModel.onEvent(EditTransactionUiEvent.OnTypeChanged(type))
                        }
                    )

                    HeroAmountCard(
                        amountInput = state.amountInput,
                        transactionType = state.transactionType,
                        onDigitPressed = { digit ->
                            viewModel.onEvent(EditTransactionUiEvent.OnDigitPressed(digit))
                        },
                        onDeletePressed = {
                            viewModel.onEvent(EditTransactionUiEvent.OnDeletePressed)
                        },
                        onDecimalPressed = {
                            viewModel.onEvent(EditTransactionUiEvent.OnDecimalPressed)
                        },
                        onQuickAmountPressed = { amount ->
                            viewModel.onEvent(
                                EditTransactionUiEvent.OnQuickAmountPressed(amount)
                            )
                        }
                    )

                    FieldRows(
                        state = state.toAddState(),
                        onEvent = { addEvent ->
                            viewModel.onEvent(addEvent.toEditEvent())
                        }
                    )
                }
            }

            // Pinned save button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF8E78FF),
                                Color(0xFF6644FF)
                            )
                        )
                    )
                    .clickable(enabled = !state.isSaving) {
                        viewModel.onEvent(EditTransactionUiEvent.OnSaveClicked)
                    },
                contentAlignment = Alignment.Center
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Save changes",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Sheets and dialogs on top
        if (state.showDeleteConfirm) {
            DeleteConfirmDialog(
                onConfirm = {
                    viewModel.onEvent(EditTransactionUiEvent.OnDeleteConfirmed)
                },
                onDismiss = {
                    viewModel.onEvent(EditTransactionUiEvent.OnDeleteDismissed)
                }
            )
        }

        if (state.showCategoryPicker) {
            CategoryPickerSheet(
                categories = state.availableCategories,
                selectedCategoryId = state.selectedCategory?.id ?: 0L,
                onCategorySelected = { category ->
                    viewModel.onEvent(EditTransactionUiEvent.OnCategorySelected(category))
                },
                onDismiss = {
                    viewModel.onEvent(EditTransactionUiEvent.OnCategoryPickerDismiss)
                },
                onCategoryCreated = { _, _, _ -> }
            )
        }

        if (state.showDatePicker) {
            PennywiseDatePicker(
                selectedDate = state.selectedDate,
                onDateSelected = { date ->
                    viewModel.onEvent(EditTransactionUiEvent.OnDateSelected(date))
                },
                onDismiss = {
                    viewModel.onEvent(EditTransactionUiEvent.OnDatePickerDismiss)
                }
            )
        }
    }
}