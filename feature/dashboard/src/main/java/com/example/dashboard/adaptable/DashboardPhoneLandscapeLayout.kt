package com.example.dashboard.adaptable


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.common.utils.CurrencyFormatter
import com.example.dashboard.DashboardUiEvent
import com.example.dashboard.DashboardUiState
import com.example.dashboard.components.BalanceHeroSection
import com.example.dashboard.components.DashboardTopBar
import com.example.dashboard.components.DonutCard
import com.example.dashboard.components.RecentTransactionsSection
import com.example.ui.PennywiseWindowLayout
import com.example.ui.components.PennywiseBottomNav
import com.example.ui.components.PennywiseRoutes
import com.example.ui.theme.Accent
import com.example.ui.theme.Background
import com.example.ui.theme.Expense
import com.example.ui.theme.Income
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.Surface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DashboardPhoneLandscapeLayout(
    state: DashboardUiState,
    onEvent: (DashboardUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Left column — balance + income/expense
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Compact balance — just numbers, no large padding
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Surface)
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "TOTAL BALANCE",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 9.sp,
                            letterSpacing = 0.8.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = CurrencyFormatter.formatWithSymbol(state.totalBalance),
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 26.sp,
                            letterSpacing = (-1).sp,
                            color = TextPrimary,
                            lineHeight = 26.sp
                        )
                    }
                }

                // Income + Expense — reuse exact same BalanceHeroSection
                // but we only want the income/expense row portion
                // Since BalanceHeroSection bundles both, we split here
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Income card
                    CompactStatCard(
                        label = "INCOME",
                        value = "+${CurrencyFormatter.formatWithSymbol(state.totalIncome)}",
                        valueColor = Income,
                        modifier = Modifier.weight(1f)
                    )
                    // Expense card
                    CompactStatCard(
                        label = "EXPENSES",
                        value = "-${CurrencyFormatter.formatWithSymbol(state.totalExpense)}",
                        valueColor = Expense,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Right column — donut card fills height
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                DonutCard(
                    totalExpense = state.totalExpense,
                    categorySpending = state.spendingByCategory
                )
            }
        }

        // Full width — recent transactions scrollable
        RecentTransactionsSection(
            transactions = state.recentTransactions,
            categoryMap = state.categoryMap,
            onSeeAllClick = {
                onEvent(DashboardUiEvent.OnSeeAllTransactionsClick)
            },
            onTransactionClick = { id ->
                onEvent(DashboardUiEvent.OnTransactionClick(id))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 8.dp, top = 8.dp)
        )
    }
}

@Composable
fun CompactStatCard(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Surface)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = label,
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 9.sp,
                letterSpacing = 0.6.sp,
                color = TextSecondary
            )
            Text(
                text = value,
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = valueColor
            )
        }
    }
}