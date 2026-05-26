package com.example.transactions.add.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.layout.FoldingFeature
import com.example.domain.model.TransactionType
import com.example.transactions.add.AddTransactionUiEvent
import com.example.transactions.add.AddTransactionUiState
import com.example.ui.components.HeroAmountCard
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.Surface
import com.example.ui.theme.TextPrimary

@Composable
fun AddTransactionFoldableLayout(
    state: AddTransactionUiState,
    foldingFeature: FoldingFeature,
    onEvent: (AddTransactionUiEvent) -> Unit
) {
    val density = LocalDensity.current
    val hingeCenterXDp = with(density) {
        foldingFeature.bounds.centerX().toDp() - 80.dp
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize()) {
            // ── Left pane — amount section ─────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // TopBar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        letterSpacing = (-0.5).sp,
                        color = TextPrimary
                    )
                }

                // Type toggle
                TypeToggle(
                    selectedType = state.transactionType,
                    onTypeChanged = { type ->
                        onEvent(AddTransactionUiEvent.OnTypeChanged(type))
                    }
                )

                // Hero amount card centred
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    HeroAmountCard(
                        amountInput = state.amountInput,
                        transactionType = state.transactionType,
                        onDigitPressed = { onEvent(AddTransactionUiEvent.OnDigitPressed(it)) },
                        onDeletePressed = { onEvent(AddTransactionUiEvent.OnDeletePressed) },
                        onDecimalPressed = { onEvent(AddTransactionUiEvent.OnDecimalPressed) },
                        onQuickAmountPressed = {
                            onEvent(AddTransactionUiEvent.OnQuickAmountPressed(it))
                        }
                    )
                }
            }

            // ── Right pane — fields + save ─────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Field rows card
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
                    FieldRows(state = state, onEvent = onEvent)
                }

                // Save button pinned to bottom of right pane
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (state.transactionType == TransactionType.EXPENSE)
                                "Save expense" else "Save income",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
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
    AddTransactionSheets(state = state, onEvent = onEvent)
}