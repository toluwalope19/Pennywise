package com.example.budgets.components


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.budgets.BudgetsUiEvent
import com.example.budgets.NewBudgetState
import com.example.budgets.amount
import com.example.domain.model.TransactionType
import com.example.ui.components.HeroAmountCard
import com.example.ui.components.PennywiseDatePicker
import com.example.ui.components.QuickAmountChip
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.PennywiseTheme
import com.example.ui.theme.Surface
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewBudgetSheet(
    state: NewBudgetState,
    onEvent: (BudgetsUiEvent) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 6.dp)
                    .width(32.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color.White.copy(alpha = 0.2f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
        ) {
            // Header
            NewBudgetHeader(onDismiss = onDismiss)

            HeroAmountCard(
                amountInput = state.amountInput,
                transactionType = TransactionType.EXPENSE, // budgets are always expense
                onDigitPressed = { digit ->
                    onEvent(BudgetsUiEvent.OnAmountDigitPressed(digit))
                },
                onDeletePressed = {
                    onEvent(BudgetsUiEvent.OnAmountDeletePressed)
                },
                onDecimalPressed = {
                    onEvent(BudgetsUiEvent.OnAmountDecimalPressed)
                },
                onQuickAmountPressed = { amount ->
                    onEvent(BudgetsUiEvent.OnQuickAmountPressed(amount))
                },
                quickChips = listOf(
                    QuickAmountChip("₦100", 100.0),
                    QuickAmountChip("₦250", 250.0),
                    QuickAmountChip("₦500", 500.0),
                    QuickAmountChip("₦1,000", 1000.0)
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            BudgetCategoryField(
                selectedCategory = state.selectedCategory,
                onClick = { onEvent(BudgetsUiEvent.OnCategoryPickerOpen) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            PeriodSegmentedControl(
                selectedPeriod = state.period,
                onPeriodChanged = { period ->
                    onEvent(BudgetsUiEvent.OnPeriodChanged(period))
                }
            )


            Spacer(modifier = Modifier.height(10.dp))

            BudgetStartDateField(
                startDate = state.startDate,
                onClick = { onEvent(BudgetsUiEvent.OnDatePickerOpen) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            NotificationsSection(
                alertsEnabled = state.alertsEnabled,
                alertThreshold = state.alertThreshold,
                onAlertsToggled = { enabled ->
                    onEvent(BudgetsUiEvent.OnAlertsToggled(enabled))
                },
                onThresholdChanged = { threshold ->
                    onEvent(BudgetsUiEvent.OnAlertThresholdChanged(threshold))
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Spacer(modifier = Modifier.height(10.dp))

            NewBudgetButtons(
                isSaving = state.isSaving,
                isValid = state.amount > 0.0 && state.selectedCategory != null,
                onCancel = onDismiss,
                onCreate = { onEvent(BudgetsUiEvent.OnCreateBudgetClicked) }
            )
        }
    }

}

// ── Header ─────────────────────────────────────────────────────────────────

@Composable
private fun NewBudgetHeader(onDismiss: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 4.dp, bottom = 20.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "New budget",
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            letterSpacing = (-0.3).sp,
            color = TextPrimary
        )
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(SurfaceElevated)
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Close",
                tint = TextPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// ── Previews ───────────────────────────────────────────────────────────────

@Preview(
    showBackground = true,
    backgroundColor = 0xFF141414
)
@Composable
private fun NewBudgetHeaderPreview() {
    PennywiseTheme {
        NewBudgetHeader(onDismiss = {})
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0A0A0A,
    device = "spec:width=390dp,height=844dp,dpi=420"
)
@Composable
private fun NewBudgetSheetPreview() {
    PennywiseTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.6f)),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(Surface)
                    .padding(bottom = 28.dp)
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 6.dp)
                        .width(32.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .align(Alignment.CenterHorizontally)
                )
                NewBudgetHeader(onDismiss = {})

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Amount + fields coming next",
                        color = TextSecondary,
                        fontFamily = InterFontFamily,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
