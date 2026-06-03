package com.example.dashboard.adaptable


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.window.layout.FoldingFeature
import com.example.dashboard.CategorySpending
import com.example.dashboard.DashboardUiEvent
import com.example.dashboard.DashboardUiState
import com.example.dashboard.components.BalanceHeroSection
import com.example.dashboard.components.DonutCard
import com.example.dashboard.components.RecentTransactionsSection
import com.example.ui.theme.Background
import com.example.ui.theme.PennywiseTheme

@Composable
fun DashboardFoldableLayout(
    state: DashboardUiState,
    foldingFeature: FoldingFeature,
    onEvent: (DashboardUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    // Hinge bounds in dp — subtract rail width (80dp)
    val hingeCenterXDp = with(density) {
        foldingFeature.bounds.centerX().toDp() - 80.dp
    }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Left pane — balance hero (includes income/expense cards)
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                item {
                    BalanceHeroSection(
                        totalBalance = state.totalBalance,
                        totalIncome = state.totalIncome,
                        totalExpense = state.totalExpense,
                        stackCards = true
                    )
                }
            }

            // Right pane — spending overview with legend + recent transactions
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                item {
                    DonutCard(
                        totalExpense = state.totalExpense,
                        categorySpending = state.spendingByCategory,
                        dotLegend = true
                    )
                }
                item {
                    RecentTransactionsSection(
                        transactions = state.recentTransactions,
                        categoryMap = state.categoryMap,
                        onSeeAllClick = {
                            onEvent(DashboardUiEvent.OnSeeAllTransactionsClick)
                        },
                        onTransactionClick = { id ->
                            onEvent(DashboardUiEvent.OnTransactionClick(id))
                        }
                    )
                }
            }
        }

        // Fold hinge visual — subtle vertical line at exact hinge position
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
                .offset(x = hingeCenterXDp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.06f),
                            Color.White.copy(alpha = 0.06f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

// ── Shared preview state ───────────────────────────────────────────────────

private val previewState = DashboardUiState(
    isLoading = false,
    totalBalance = 8247.32,
    totalIncome = 2092.0,
    totalExpense = 671.89,
    selectedMonth = 5,
    selectedYear = 2026,
    spendingByCategory = listOf(
        CategorySpending("Food", 214.84, 32f),
        CategorySpending("Health", 147.82, 22f),
        CategorySpending("Transport", 107.50, 16f),
        CategorySpending("Shopping", 94.06, 14f),
        CategorySpending("Education", 60.47, 9f)
    ),
    recentTransactions = emptyList(),
    categoryMap = emptyMap()
)

// ── Phone portrait ─────────────────────────────────────────────────────────

@Preview(
    name = "Phone Portrait",
    showBackground = true,
    backgroundColor = 0xFF0A0A0A,
    device = "spec:width=390dp,height=844dp,dpi=420"
)
@Composable
private fun DashboardPhonePortraitPreview() {
    PennywiseTheme {
        DashboardPhonePortraitLayout(
            state = previewState,
            onEvent = {}
        )
    }
}

// ── Phone landscape ────────────────────────────────────────────────────────

@Preview(
    name = "Phone Landscape",
    showBackground = true,
    backgroundColor = 0xFF0A0A0A,
    device = "spec:width=844dp,height=390dp,dpi=420"
)
@Composable
private fun DashboardPhoneLandscapePreview() {
    PennywiseTheme {
        DashboardPhoneLandscapeLayout(
            state = previewState,
            onEvent = {}
        )
    }
}

// ── Tablet portrait ────────────────────────────────────────────────────────

@Preview(
    name = "Tablet Portrait",
    showBackground = true,
    backgroundColor = 0xFF0A0A0A,
    device = "spec:width=768dp,height=1024dp,dpi=320"
)
@Composable
private fun DashboardTabletPortraitPreview() {
    PennywiseTheme {
        DashboardTabletPortraitLayout(
            state = previewState,
            onEvent = {}
        )
    }
}

// ── Tablet landscape ───────────────────────────────────────────────────────

@Preview(
    name = "Tablet Landscape",
    showBackground = true,
    backgroundColor = 0xFF0A0A0A,
    device = "spec:width=1024dp,height=768dp,dpi=320"
)
@Composable
private fun DashboardTabletLandscapePreview() {
    PennywiseTheme {
        DashboardTabletLandscapeLayout(
            state = previewState,
            onEvent = {}
        )
    }
}

// ── Foldable ───────────────────────────────────────────────────────────────
// FoldingFeature requires a real device — preview the layout structure only

@Preview(
    name = "Foldable (simulated)",
    showBackground = true,
    backgroundColor = 0xFF0A0A0A,
    device = "spec:width=884dp,height=1080dp,dpi=420"
)
@Composable
private fun DashboardFoldablePreview() {
    PennywiseTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    item {
                        BalanceHeroSection(
                            totalBalance = previewState.totalBalance,
                            totalIncome = previewState.totalIncome,
                            totalExpense = previewState.totalExpense,
                            stackCards = true
                        )
                    }
                }
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    item {
                        DonutCard(
                            totalExpense = previewState.totalExpense,
                            categorySpending = previewState.spendingByCategory,
                            dotLegend = true
                        )
                    }
                }
            }
        }
    }
}