package com.example.transactions.list

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.transactions.components.FilterChipsRow
import com.example.transactions.components.MasterDetailEditPanel
import com.example.transactions.components.MasterDetailIconButton
import com.example.transactions.components.MasterDetailTransactionList
import com.example.transactions.components.TransactionsContent
import com.example.ui.PennywiseWindowLayout
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TransactionsMasterDetailLayout(
    state: TransactionsUiState,
    transactions: LazyPagingItems<TransactionListItem>,
    onEvent: (TransactionsUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {

        // ── Left panel — 40% ──────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.4f)
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(0.dp)
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Activity",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    letterSpacing = (-0.4).sp,
                    color = TextPrimary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    MasterDetailIconButton(icon = Icons.Rounded.Search, onClick = {})
                    MasterDetailIconButton(icon = Icons.Rounded.Tune, onClick = {})
                }
            }

            // Filter chips — reuse existing composable
            FilterChipsRow(
                selectedFilter = state.selectedFilter,
                selectedMonth = state.selectedMonth,
                selectedYear = state.selectedYear,
                onFilterChanged = { filter ->
                    onEvent(TransactionsUiEvent.OnFilterChanged(filter))
                }
            )

            // Transaction list
            MasterDetailTransactionList(
                transactions = transactions,
                categoryMap = state.categoryMap,
                selectedTransactionId = state.selectedTransactionId,
                onTransactionSelected = { id ->
                    onEvent(TransactionsUiEvent.OnTransactionSelected(id))
                },
                modifier = Modifier.weight(1f)
            )
        }

        // ── Right panel — 60% ─────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.6f)
        ) {
            if (state.selectedTransactionId != null) {
                key(state.selectedTransactionId) {
                    MasterDetailEditPanel(
                        state.selectedTransactionId,
                        onDeleted = {
                            onEvent(TransactionsUiEvent.OnSelectionCleared)
                        }
                    )
                }
            } else {
                // Empty state — nothing selected yet
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.TouchApp,
                        contentDescription = null,
                        tint = TextSecondary.copy(alpha = 0.3f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Select a transaction to edit",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}