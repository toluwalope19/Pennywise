package com.example.ui.components



import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.ui.theme.Accent
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.Surface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PennywiseDatePicker(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = java.time.Instant
                            .ofEpochMilli(millis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                        onDateSelected(date)
                    }
                }
            ) {
                Text(
                    text = "Confirm",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = Accent
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Normal,
                    color = TextSecondary
                )
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor = Surface
        )
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = Surface,
                titleContentColor = TextSecondary,
                headlineContentColor = TextPrimary,
                weekdayContentColor = TextSecondary,
                dayContentColor = TextPrimary,
                selectedDayContainerColor = Accent,
                selectedDayContentColor = Color.White,
                todayDateBorderColor = Accent,
                todayContentColor = Accent
            )
        )
    }
}