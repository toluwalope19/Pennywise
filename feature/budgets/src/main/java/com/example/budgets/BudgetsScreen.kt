package com.example.budgets


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.PieChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.budgets.components.BudgetCard
import com.example.budgets.components.BudgetSummaryCard
import com.example.budgets.components.BudgetsEmptyState
import com.example.budgets.components.BudgetsTopBar
import com.example.budgets.components.NewBudgetSheet
import com.example.domain.model.Budget
import com.example.ui.PennywiseWindowLayout
import com.example.ui.components.CategoryPickerSheet
import com.example.ui.components.PennywiseDatePicker
import com.example.ui.theme.Accent
import com.example.ui.theme.Background
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.PennywiseTheme
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.utils.StaggeredAnimatedItem

@Composable
fun BudgetsScreen(
    windowLayout: PennywiseWindowLayout = PennywiseWindowLayout.PhonePortrait,
    viewModel: BudgetsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val newBudgetState by viewModel.newBudgetState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is BudgetsUiEffect.NavigateToBudgetDetail -> { /* later */ }
                is BudgetsUiEffect.ShowError -> { /* Snackbar later */ }
            }
        }
    }


    BudgetsContent(
        state = state,
        newBudgetState = newBudgetState,
        windowLayout = windowLayout,
        onEvent = viewModel::onEvent
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BudgetsContent(
    state: BudgetsUiState,
    newBudgetState: NewBudgetState,
    windowLayout: PennywiseWindowLayout,
    onEvent: (BudgetsUiEvent) -> Unit
) {
    Scaffold(
        containerColor = Background,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            BudgetsTopBar(
                selectedMonth = state.selectedMonth,
                selectedYear = state.selectedYear,
                onMonthChanged = { direction ->
                    onEvent(BudgetsUiEvent.OnMonthChanged(direction))
                },
                onAddClick = { onEvent(BudgetsUiEvent.OnAddBudgetClick) }
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

        if (state.budgets.isEmpty()) {
            BudgetsEmptyState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                onAddClick = { onEvent(BudgetsUiEvent.OnAddBudgetClick) }
            )
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {
                BudgetSummaryCard(state = state)
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Categories",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "${state.budgets.size} active",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            val isWideLayout = windowLayout is PennywiseWindowLayout.TabletLandscape ||
                    windowLayout is PennywiseWindowLayout.Foldable


            if (isWideLayout) {
                // ← 2-column grid using chunked
                items(state.budgets.chunked(2)) { rowBudgets ->
                    val rowIndex = state.budgets.chunked(2).indexOf(rowBudgets)
                    StaggeredAnimatedItem(index = rowIndex) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowBudgets.forEach { budget ->
                                BudgetCard(
                                    budgetWithSpending = budget,
                                    onEvent = onEvent,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (rowBudgets.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            } else {
                items(state.budgets) { budgetWithSpending ->
                    val index = state.budgets.indexOf(budgetWithSpending)
                    StaggeredAnimatedItem(index = index) {
                        BudgetCard(
                            budgetWithSpending = budgetWithSpending,
                            onEvent = onEvent
                        )
                    }
                }
            }

        }
    }

    // New budget sheet — after UI is built
    if (state.showNewBudgetSheet) {
        NewBudgetSheet(
            state = newBudgetState,
                onEvent = onEvent,
            onDismiss = { onEvent(BudgetsUiEvent.OnNewBudgetDismiss) }
        )
    }

    if (newBudgetState.showCategoryPicker) {
        CategoryPickerSheet(
            categories = newBudgetState.availableCategories,
            selectedCategoryId = newBudgetState.selectedCategory?.id ?: 0L,
            onCategorySelected = { category ->
                onEvent(BudgetsUiEvent.OnCategorySelected(category))
            },
            onDismiss = {
                onEvent(BudgetsUiEvent.OnCategoryPickerDismiss)
            },
            onCategoryCreated = { name, color, icon ->
                // Not needed for budgets
            }
        )
    }

    if (newBudgetState.showDatePicker) {
        PennywiseDatePicker(
            selectedDate = newBudgetState.startDate,
            onDateSelected = { date ->
                onEvent(BudgetsUiEvent.OnDateSelected(date))
            },
            onDismiss = {
                onEvent(BudgetsUiEvent.OnDatePickerDismiss)
            }
        )
    }
}


//@Preview(
//    showBackground = true,
//    backgroundColor = 0xFF0A0A0A
//)
//@Composable
//private fun BudgetsTopBarPreview() {
//    PennywiseTheme {
//        BudgetsTopBar(onAddClick = {})
//    }
//}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0A0A0A,
    device = "spec:width=390dp,height=844dp,dpi=420"
)
@Composable
private fun BudgetsEmptyStatePreview() {
    PennywiseTheme {
        BudgetsEmptyState(
            onAddClick = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0A0A0A
)
@Composable
private fun BudgetSummaryCardPreview() {
    PennywiseTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            BudgetSummaryCard(
                state = BudgetsUiState(
                    selectedMonth = 5,
                    selectedYear = 2026,
                    totalBudget = 1850.0,
                    totalSpent = 1224.0,
                    budgets = emptyList()
                )
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0A0A0A
)
@Composable
private fun BudgetCardPreview() {
    PennywiseTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Normal state
                BudgetCard(
                    budgetWithSpending = BudgetWithSpending(
                        budget = Budget(
                            id = 1L,
                            categoryId = 1L,
                            amount = 400.0,
                            month = 5,
                            year = 2026
                        ),
                        category = null,
                        spent = 320.0,
                        percentage = 0.8f,
                        isOverBudget = false
                    ),
                    onEvent = {}
                )
                // Over budget state
                BudgetCard(
                    budgetWithSpending = BudgetWithSpending(
                        budget = Budget(
                            id = 2L,
                            categoryId = 2L,
                            amount = 200.0,
                            month = 5,
                            year = 2026
                        ),
                        category = null,
                        spent = 218.0,
                        percentage = 1.09f,
                        isOverBudget = true
                    ),
                    onEvent = {}
                )
            }
        }
    }
}