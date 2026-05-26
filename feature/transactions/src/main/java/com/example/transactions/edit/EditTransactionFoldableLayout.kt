package com.example.transactions.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.layout.FoldingFeature
import com.example.transactions.add.components.FieldRows
import com.example.transactions.add.components.TypeToggle
import com.example.ui.components.HeroAmountCard
import com.example.ui.theme.Expense
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.Surface
import com.example.ui.theme.TextPrimary

@Composable
fun EditTransactionFoldableLayout(
    state: EditTransactionUiState,
    foldingFeature: FoldingFeature,
    onEvent: (EditTransactionUiEvent) -> Unit
) {
    val density = LocalDensity.current
    val hingeCenterXDp = with(density) {
        foldingFeature.bounds.centerX().toDp() - 80.dp
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Left pane — title + type toggle + amount
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // TopBar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { onEvent(EditTransactionUiEvent.OnBackClicked) }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                    Text(
                        text = "Edit transaction",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        letterSpacing = (-0.5).sp,
                        color = TextPrimary
                    )
                    IconButton(
                        onClick = { onEvent(EditTransactionUiEvent.OnDeleteClicked) }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.DeleteOutline,
                            contentDescription = "Delete",
                            tint = Expense,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                TypeToggle(
                    selectedType = state.transactionType,
                    onTypeChanged = { type ->
                        onEvent(EditTransactionUiEvent.OnTypeChanged(type))
                    }
                )

                if (!state.isLoading) {
                    HeroAmountCard(
                        amountInput = state.amountInput,
                        transactionType = state.transactionType,
                        onDigitPressed = { digit ->
                            onEvent(EditTransactionUiEvent.OnDigitPressed(digit))
                        },
                        onDeletePressed = {
                            onEvent(EditTransactionUiEvent.OnDeletePressed)
                        },
                        onDecimalPressed = {
                            onEvent(EditTransactionUiEvent.OnDecimalPressed)
                        },
                        onQuickAmountPressed = { amount ->
                            onEvent(EditTransactionUiEvent.OnQuickAmountPressed(amount))
                        }
                    )
                }
            }

            // Right pane — fields + save button
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Surface)
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FieldRows(
                        state = state.toAddState(),
                        onEvent = { addEvent -> onEvent(addEvent.toEditEvent()) }
                    )
                }

                // Save button pinned to bottom
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF8E78FF),
                                    Color(0xFF6644FF)
                                )
                            )
                        )
                        .clickable(enabled = !state.isSaving) {
                            onEvent(EditTransactionUiEvent.OnSaveClicked)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Save changes",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Fold hinge line
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

    // Sheets
    EditTransactionSheets(state = state, onEvent = onEvent)
}