package com.example.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Summarize
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.settings.SettingsUiEvent
import com.example.settings.SettingsUiState
import com.example.settings.components.ProfileSection
import com.example.settings.components.SectionLabel
import com.example.settings.components.SettingsDivider
import com.example.settings.components.SettingsGroupCard
import com.example.settings.components.SettingsRow
import com.example.settings.components.SettingsSwitch
import com.example.settings.components.TrailingChevron
import com.example.settings.components.TrailingText
import com.example.ui.PennywiseWindowLayout
import com.example.ui.theme.Background
import com.example.ui.theme.Expense
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.PennywiseTheme
import com.example.ui.theme.TextPrimary

@Composable
fun SettingsSingleColumnLayout(
    state: SettingsUiState,
    onEvent: (SettingsUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ProfileSection(state = state)
        SectionLabel(text = "Preferences")
        SettingsGroupCard {
            SettingsRow(
                icon = Icons.Rounded.Payments,
                iconGradient = Brush.linearGradient(
                    listOf(Color(0xFFFFD25A), Color(0xFFE8A625))
                ),
                label = "Currency",
                trailing = { TrailingText("${state.currencySymbol} ${state.currencyName}") },
                onClick = { onEvent(SettingsUiEvent.OnCurrencyClicked) }
            )
            SettingsDivider()
            SettingsRow(
                icon = Icons.Rounded.DarkMode,
                iconGradient = Brush.linearGradient(
                    listOf(Color(0xFF4FD1FF), Color(0xFF2680E8))
                ),
                label = "Theme",
                trailing = { TrailingText("Dark") },
                onClick = { onEvent(SettingsUiEvent.OnThemeClicked) }
            )
        }
        SectionLabel(text = "Notifications")
        SettingsGroupCard {
            SettingsRow(
                icon = Icons.Rounded.Notifications,
                iconGradient = Brush.linearGradient(
                    listOf(Color(0xFFFF7AC1), Color(0xFFA14BFF))
                ),
                label = "Spending alerts",
                trailing = {
                    SettingsSwitch(
                        checked = state.spendingAlertsEnabled,
                        onCheckedChange = {
                            onEvent(SettingsUiEvent.OnSpendingAlertsToggled(it))
                        }
                    )
                },
                onClick = {
                    onEvent(SettingsUiEvent.OnSpendingAlertsToggled(!state.spendingAlertsEnabled))
                }
            )
            SettingsDivider()
            SettingsRow(
                icon = Icons.Rounded.Summarize,
                iconGradient = Brush.linearGradient(
                    listOf(Color(0xFF5AE9C8), Color(0xFF0BA67B))
                ),
                label = "Weekly summary",
                trailing = {
                    SettingsSwitch(
                        checked = state.weeklySummaryEnabled,
                        onCheckedChange = {
                            onEvent(SettingsUiEvent.OnWeeklySummaryToggled(it))
                        }
                    )
                },
                onClick = {
                    onEvent(SettingsUiEvent.OnWeeklySummaryToggled(!state.weeklySummaryEnabled))
                }
            )
        }
        SectionLabel(text = "Data")
        SettingsGroupCard {
            SettingsRow(
                icon = Icons.Rounded.Download,
                iconGradient = Brush.linearGradient(
                    listOf(Color(0xFFB79CFF), Color(0xFF5B36D1))
                ),
                label = "Export as CSV",
                trailing = { TrailingChevron() },
                onClick = { onEvent(SettingsUiEvent.OnExportCSVClicked) }
            )
            SettingsDivider()
            SettingsRow(
                icon = Icons.Rounded.Delete,
                iconGradient = Brush.linearGradient(
                    listOf(Color(0xFFFF8E8E), Color(0xFFE63946))
                ),
                label = "Clear all data",
                labelColor = Expense,
                trailing = { TrailingChevron() },
                onClick = { onEvent(SettingsUiEvent.OnClearDataClicked) }
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}