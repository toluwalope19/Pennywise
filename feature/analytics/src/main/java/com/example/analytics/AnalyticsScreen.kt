package com.example.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.analytics.components.AnalyticsSingleColumnLayout
import com.example.analytics.components.AnalyticsTabletLayout
import com.example.analytics.components.IncomeExpenseBarChart
import com.example.analytics.components.LegendDot
import com.example.analytics.components.SpendingBreakdownCard
import com.example.analytics.components.TotalStatCard
import com.example.ui.PennywiseWindowLayout
import com.example.ui.theme.Accent
import com.example.ui.theme.Background
import com.example.ui.theme.Expense
import com.example.ui.theme.Income
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.PennywiseTheme
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun AnalyticsScreen(
    windowLayout: PennywiseWindowLayout = PennywiseWindowLayout.PhonePortrait,
    viewModel: AnalyticsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                AnalyticsUiEffect.ExportCSV -> { /* defer */ }
                is AnalyticsUiEffect.ShowError -> { /* Snackbar later */ }
            }
        }
    }

    AnalyticsContent(
        state = state,
        windowLayout = windowLayout,
        onEvent = viewModel::onEvent
    )
}

// ── Screen content ─────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnalyticsContent(
    state: AnalyticsUiState,
    windowLayout: PennywiseWindowLayout,
    onEvent: (AnalyticsUiEvent) -> Unit
) {
    Scaffold(
        containerColor = Background,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(top = 0),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background
                ),
                title = {
                    Text(
                        text = "Analytics",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TextPrimary,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                },
                actions = {
                    IconButton(
                        onClick = { onEvent(AnalyticsUiEvent.OnExportClicked) }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.FileDownload,
                            contentDescription = "Export",
                            tint = TextPrimary
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

        val isWideLayout = windowLayout is PennywiseWindowLayout.TabletLandscape ||
                windowLayout is PennywiseWindowLayout.Foldable

        if (isWideLayout) {
            AnalyticsTabletLayout(
                state = state,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            AnalyticsSingleColumnLayout(
                state = state,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
private fun LegendDotPreview() {
    PennywiseTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            LegendDot(color = Income, label = "Income")
            LegendDot(color = Expense, label = "Expenses")
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
private fun TotalStatCardPreview() {
    PennywiseTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TotalStatCard(
                label = "INCOME · 6 MO",
                value = "+₦14,820",
                valueColor = Income,
                modifier = Modifier.weight(1f)
            )
            TotalStatCard(
                label = "EXPENSES · 6 MO",
                value = "-₦9,142",
                valueColor = Expense,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0A0A0A,
    device = "spec:width=390dp,height=844dp,dpi=420"
)
@Composable
private fun AnalyticsScreenPreview() {
    PennywiseTheme {
        AnalyticsContent(
            state = AnalyticsUiState(
                isLoading = false,
                monthlyData = listOf(
                    MonthlyData(YearMonth.now().minusMonths(5), 2400.0, 1200.0),
                    MonthlyData(YearMonth.now().minusMonths(4), 2800.0, 1800.0),
                    MonthlyData(YearMonth.now().minusMonths(3), 3200.0, 2100.0),
                    MonthlyData(YearMonth.now().minusMonths(2), 2900.0, 1600.0),
                    MonthlyData(YearMonth.now().minusMonths(1), 3800.0, 2400.0),
                    MonthlyData(YearMonth.now(), 4200.0, 1800.0)
                ),
                totalIncome6Months = 19300.0,
                totalExpense6Months = 10900.0,
                spendingBreakdown = listOf(
                    CategoryBreakdown("Food", 0xFFFF8A3DL, 214.84, 32f),
                    CategoryBreakdown("Health", 0xFF5AE9C8L, 147.82, 22f),
                    CategoryBreakdown("Transport", 0xFF4FD1FFL, 107.50, 16f),
                    CategoryBreakdown("Shopping", 0xFFFF7AC1L, 94.06, 14f),
                    CategoryBreakdown("Education", 0xFFB79CFFL, 60.47, 9f)
                )
            ),
            onEvent = {},
            windowLayout = PennywiseWindowLayout.PhonePortrait
        )
    }
}