package com.example.transactions.add.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.DocumentScanner
import androidx.compose.material.icons.rounded.EditCalendar
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.transactions.add.AddTransactionUiEvent
import com.example.transactions.add.AddTransactionUiState
import com.example.ui.components.CategoryIcon
import com.example.ui.components.CategoryIconShape
import com.example.ui.components.toDisplay
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.PennywiseTheme
import com.example.ui.theme.Surface
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.time.LocalDate

@Composable
fun FieldRow(
    leadingContent: @Composable () -> Unit,
    label: String,
    value: String,
    valueColor: Color = TextPrimary,
    trailingIcon: ImageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.04f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Leading icon slot
        leadingContent()

        // Label + value
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                letterSpacing = 0.8.sp,
                color = TextSecondary
            )
            Text(
                text = value,
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = valueColor,
                modifier = Modifier.offset(y = (-4).dp)
            )
        }

        // Trailing chevron
        Icon(
            imageVector = trailingIcon,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}

// ── Muted leading icon ─────────────────────────────────────────────────────

@Composable
private fun MutedLeadingIcon(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceElevated),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(22.dp)
        )
    }
}

// ── All four field rows ────────────────────────────────────────────────────

@Composable
fun FieldRows(
    state: AddTransactionUiState,
    onEvent: (AddTransactionUiEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Category
        val categoryDisplay = state.selectedCategory?.toDisplay()
        FieldRow(
            leadingContent = {
                if (categoryDisplay != null) {
                    CategoryIcon(
                        display = categoryDisplay,
                        size = 40.dp,
                        shape = CategoryIconShape.ROUNDED_SQUARE
                    )
                } else {
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                            .background(SurfaceElevated)
                    )
                }
            },
            label = "CATEGORY",
            value = categoryDisplay?.name ?: "Select category",
            onClick = { onEvent(AddTransactionUiEvent.OnCategoryPickerOpen) }
        )

        // Date
        val dateText = when (state.selectedDate) {
            LocalDate.now() -> "Today · ${formatDate(state.selectedDate)}"
            LocalDate.now().minusDays(1) -> "Yesterday · ${formatDate(state.selectedDate)}"
            else -> formatDate(state.selectedDate)
        }

        FieldRow(
            leadingContent = {
                MutedLeadingIcon(Icons.Rounded.CalendarToday)
            },
            label = "DATE",
            value = dateText,
            trailingIcon = Icons.Rounded.EditCalendar,
            onClick = { onEvent(AddTransactionUiEvent.OnDatePickerOpen) }
        )

        // Receipt scan
        FieldRow(
            leadingContent = {
                MutedLeadingIcon(Icons.Rounded.DocumentScanner)
            },
            label = "RECEIPT",
            value = if (state.isScanning) "Scanning..." else "Scan to auto-fill",
            valueColor = TextSecondary,
            trailingIcon = Icons.Rounded.PhotoCamera,
            onClick = { /* ML Kit — wired later */ }
        )

        // Note
        NoteField(
            note = state.note,
            onNoteChanged = { note ->
                onEvent(AddTransactionUiEvent.OnNoteChanged(note))
            }
        )
    }
}

// ── Note field — editable ──────────────────────────────────────────────────

@Composable
private fun NoteField(
    note: String,
    onNoteChanged: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.04f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MutedLeadingIcon(Icons.Rounded.EditNote)

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "NOTE",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                letterSpacing = 0.8.sp,
                color = TextSecondary
            )
            BasicTextField(
                value = note,
                onValueChange = onNoteChanged,
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = TextPrimary
                ),
                decorationBox = { innerTextField ->
                    if (note.isEmpty()) {
                        Text(
                            text = "Write more, describe the details",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 15.sp,
                            color = TextSecondary.copy(alpha = 0.6f)
                        )
                    }
                    innerTextField()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 1.dp)
            )
        }
    }
}

// ── Date formatter ─────────────────────────────────────────────────────────

private fun formatDate(date: LocalDate): String {
    val day = date.dayOfMonth
    val month = date.month.getDisplayName(
        java.time.format.TextStyle.SHORT,
        java.util.Locale.getDefault()
    )
    val year = date.year
    return "$day $month $year"
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0A0A0A
)
@Composable
private fun FieldRowsPreview() {
    PennywiseTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            FieldRows(
                state = AddTransactionUiState(),
                onEvent = {}
            )
        }
    }
}