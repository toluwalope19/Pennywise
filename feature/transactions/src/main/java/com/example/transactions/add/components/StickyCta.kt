package com.example.transactions.add.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CategoryType
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import androidx.core.graphics.toColorInt
import com.example.domain.model.TransactionType
import com.example.ui.theme.Background
import com.example.ui.theme.PennywiseTheme

@Composable
fun StickyCta(
    transactionType: TransactionType,
    isSaving: Boolean,
    onSaveClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isExpense = transactionType == TransactionType.EXPENSE

    val gradientColors = if (isExpense) {
        listOf(Color(0xFF8E78FF), Color(0xFF6644FF))
    } else {
        listOf(Color(0xFF00E5A0), Color(0xFF0BA67B))
    }

    val glowColor = if (isExpense) {
        Color(0xFF7B61FF)
    } else {
        Color(0xFF00E5A0)
    }

    val label = if (isExpense) "Save expense" else "Save income"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Background.copy(alpha = 0.9f),
                        Background
                    )
                )
            )
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.linearGradient(colors = gradientColors)
                )
                .clickable(enabled = !isSaving) { onSaveClicked() },
            contentAlignment = Alignment.Center
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = label,
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
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0A0A0A
)
@Composable
private fun StickyCTAExpensePreview() {
    PennywiseTheme {
        StickyCta(
            transactionType = TransactionType.EXPENSE,
            isSaving = false,
            onSaveClicked = {}
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0A0A0A
)
@Composable
private fun StickyCTAIncomePreview() {
    PennywiseTheme {
        StickyCta(
            transactionType = TransactionType.INCOME,
            isSaving = false,
            onSaveClicked = {}
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0A0A0A
)
@Composable
private fun StickyCTASavingPreview() {
    PennywiseTheme {
        StickyCta(
            transactionType = TransactionType.EXPENSE,
            isSaving = true,
            onSaveClicked = {}
        )
    }
}