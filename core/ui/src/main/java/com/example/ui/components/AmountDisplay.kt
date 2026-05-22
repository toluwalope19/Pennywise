package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun AmountDisplay(
    amountInput: String,
    accentColor: Color,
    onDigitPressed: (String) -> Unit,
    onDeletePressed: () -> Unit,
    onDecimalPressed: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Blinking cursor animation
    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 550, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor_blink"
    )

    val parts = amountInput.split(".")
    val whole = parts[0]
    val cents = parts.getOrNull(1)
    val showDecimal = amountInput.contains(".")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                focusRequester.requestFocus()
                keyboardController?.show()
            },
        contentAlignment = Alignment.Center
    ) {
        // Hidden BasicTextField — captures software keyboard input via IME
        BasicTextField(
            value = TextFieldValue(amountInput, TextRange(amountInput.length)),
            onValueChange = { new ->
                val newText = new.text
                when {
                    newText.length == amountInput.length + 1 && newText.startsWith(amountInput) -> {
                        val ch = newText.last()
                        when {
                            ch.isDigit() -> onDigitPressed(ch.toString())
                            ch == '.' || ch == ',' -> onDecimalPressed()
                        }
                    }
                    newText.length < amountInput.length -> onDeletePressed()
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier
                .size(1.dp)
                .focusRequester(focusRequester),
            cursorBrush = SolidColor(Color.Transparent)
        )

        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "₦",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = accentColor,
                letterSpacing = (-1).sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = whole,
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Black,
                fontSize = 68.sp,
                color = TextPrimary,
                letterSpacing = (-3).sp,
                lineHeight = 68.sp
            )
            if (showDecimal) {
                Text(
                    text = ".${cents ?: ""}",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = TextSecondary,
                    letterSpacing = (-1).sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            Spacer(modifier = Modifier.width(2.dp))
            Box(
                modifier = Modifier
                    .padding(bottom = 6.dp)
                    .width(3.dp)
                    .height(56.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accentColor.copy(alpha = cursorAlpha))
            )
        }
    }

    LaunchedEffect(Unit) {
        delay(300)
        focusRequester.requestFocus()
        keyboardController?.show()
    }
}