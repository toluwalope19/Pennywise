package com.example.settings


data class SettingsUiState(
    val userName: String = "Alex Park",
    val userEmail: String = "alex@example.com",
    val currencySymbol: String = "₦",
    val currencyName: String = "Naira",
    val isDarkTheme: Boolean = true,
    val spendingAlertsEnabled: Boolean = true,
    val weeklySummaryEnabled: Boolean = false
)

sealed class SettingsUiEvent {
    data object OnBackClicked : SettingsUiEvent()
    data class OnSpendingAlertsToggled(val enabled: Boolean) : SettingsUiEvent()
    data class OnWeeklySummaryToggled(val enabled: Boolean) : SettingsUiEvent()
    data object OnCurrencyClicked : SettingsUiEvent()
    data object OnThemeClicked : SettingsUiEvent()
    data object OnExportCSVClicked : SettingsUiEvent()
    data object OnClearDataClicked : SettingsUiEvent()
    data object OnClearDataConfirmed : SettingsUiEvent()
}

sealed class SettingsUiEffect {
    data object NavigateBack : SettingsUiEffect()
    data object ExportCSV : SettingsUiEffect()
    data class ShowError(val message: String) : SettingsUiEffect()
}