package com.example.budgets.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.budgets.BudgetsUiEvent
import com.example.budgets.NewBudgetState
import com.example.domain.model.TransactionType
import com.example.ui.components.HeroAmountCard
import com.example.ui.components.PennywiseDatePicker
import com.example.ui.components.QuickAmountChip
import com.example.ui.theme.Border
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.PennywiseTheme
import com.example.ui.theme.Surface
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun NewBudgetButtons(
    isSaving: Boolean,
    isValid: Boolean,
    onCancel: () -> Unit,
    onCreate: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp, top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Cancel
        Box(
            modifier = Modifier
                .weight(1f)
                .height(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceElevated)
                .border(
                    width = 1.dp,
                    color = Border,
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable { onCancel() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Cancel",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = TextPrimary
            )
        }

        // Create budget
        Box(
            modifier = Modifier
                .weight(2f)
                .height(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = if (isValid) {
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF8E78FF),
                                Color(0xFF6644FF)
                            )
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF8E78FF).copy(alpha = 0.4f),
                                Color(0xFF6644FF).copy(alpha = 0.4f)
                            )
                        )
                    }
                )
                .clickable(enabled = isValid && !isSaving) { onCreate() },
            contentAlignment = Alignment.Center
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Create budget",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (isValid) Color.White
                    else Color.White.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0A0A0A
)
@Composable
private fun NewBudgetButtonsEnabledPreview() {
    PennywiseTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            NewBudgetButtons(
                isSaving = false,
                isValid = true,
                onCancel = {},
                onCreate = {}
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0A0A0A
)
@Composable
private fun NewBudgetButtonsDisabledPreview() {
    PennywiseTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            NewBudgetButtons(
                isSaving = false,
                isValid = false,
                onCancel = {},
                onCreate = {}
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0A0A0A
)
@Composable
private fun NewBudgetButtonsSavingPreview() {
    PennywiseTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            NewBudgetButtons(
                isSaving = true,
                isValid = true,
                onCancel = {},
                onCreate = {}
            )
        }
    }
}