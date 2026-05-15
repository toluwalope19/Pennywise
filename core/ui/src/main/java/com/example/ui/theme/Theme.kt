package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val PennywiseDarkColorScheme = darkColorScheme(
    primary = Accent,
    onPrimary = TextPrimary,
    primaryContainer = AccentVariant,
    background = Background,
    surface = Surface,
    surfaceVariant = SurfaceElevated,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = Border,
    error = Expense
)

// Custom colors not in M3 scheme — accessed via LocalPennywiseColors
data class PennywiseColors(
    val income: Color = Income,
    val expense: Color = Expense,
    val textSecondary: Color = TextSecondary,
    val border: Color = Border,
    val surfaceElevated: Color = SurfaceElevated
)

val LocalPennywiseColors = staticCompositionLocalOf { PennywiseColors() }

@Composable
fun PennywiseTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalPennywiseColors provides PennywiseColors()
    ) {
        MaterialTheme(
            colorScheme = PennywiseDarkColorScheme,
            typography = PennywiseTypography,
            content = content
        )
    }
}

// Convenience accessor — use anywhere in UI
object PennywiseTheme {
    val colors: PennywiseColors
        @Composable get() = LocalPennywiseColors.current
}