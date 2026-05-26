package com.example.transactions.add.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.TransactionType
import com.example.transactions.add.AddTransactionUiEvent
import com.example.transactions.add.AddTransactionUiState
import com.example.ui.components.HeroAmountCard
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.Surface
import com.example.ui.theme.TextPrimary

@Composable
fun AddTransactionTabletLandscapeLayout(
    state: AddTransactionUiState,
    onEvent: (AddTransactionUiEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f)),
        contentAlignment = Alignment.Center
    ) {
        // Floating modal card
        Column(
            modifier = Modifier
                .width(480.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Surface)
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.06f),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            // TopBar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, end = 16.dp, top = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onEvent(AddTransactionUiEvent.OnBackClicked) }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Text(
                    text = "New transaction",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                // Spacer to balance back button
                Spacer(modifier = Modifier.size(48.dp))
            }

            // Type toggle
            TypeToggle(
                selectedType = state.transactionType,
                onTypeChanged = { type ->
                    onEvent(AddTransactionUiEvent.OnTypeChanged(type))
                },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Scrollable body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HeroAmountCard(
                    amountInput = state.amountInput,
                    transactionType = state.transactionType,
                    onDigitPressed = { onEvent(AddTransactionUiEvent.OnDigitPressed(it)) },
                    onDeletePressed = { onEvent(AddTransactionUiEvent.OnDeletePressed) },
                    onDecimalPressed = { onEvent(AddTransactionUiEvent.OnDecimalPressed) },
                    onQuickAmountPressed = { onEvent(AddTransactionUiEvent.OnQuickAmountPressed(it)) }
                )
                FieldRows(state = state, onEvent = onEvent)
            }

            // Save button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = if (state.transactionType == TransactionType.EXPENSE) {
                                listOf(Color(0xFF8E78FF), Color(0xFF6644FF))
                            } else {
                                listOf(Color(0xFF00E5A0), Color(0xFF0BA67B))
                            }
                        )
                    )
                    .clickable(enabled = !state.isSaving) {
                        onEvent(AddTransactionUiEvent.OnSaveClicked)
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
                    Text(
                        text = if (state.transactionType == TransactionType.EXPENSE)
                            "Save expense" else "Save income",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            }
        }
    }

    // Sheets outside modal
    AddTransactionSheets(state = state, onEvent = onEvent)
}