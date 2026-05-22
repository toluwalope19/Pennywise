package com.example.settings


import androidx.lifecycle.viewModelScope
import com.example.common.mvi.MviViewModel
import com.example.domain.repository.PreferencesRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : MviViewModel<SettingsUiState, SettingsUiEvent, SettingsUiEffect>(
    initialState = SettingsUiState()
) {
    init {
        loadPreferences()
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            preferencesRepository.getHasSeenOnboarding() // warm up
            // Load saved preferences into state
            // Currency and notifications from DataStore
        }
    }

    override fun handleEvent(event: SettingsUiEvent) {
        when (event) {
            SettingsUiEvent.OnBackClicked ->
                setEffect(SettingsUiEffect.NavigateBack)

            is SettingsUiEvent.OnSpendingAlertsToggled -> {
                viewModelScope.launch {
                    preferencesRepository.setSpendingAlerts(event.enabled)
                    setState { copy(spendingAlertsEnabled = event.enabled) }
                }
            }

            is SettingsUiEvent.OnWeeklySummaryToggled -> {
                viewModelScope.launch {
                    preferencesRepository.setWeeklySummary(event.enabled)
                    setState { copy(weeklySummaryEnabled = event.enabled) }
                }
            }

            SettingsUiEvent.OnCurrencyClicked -> {
                // Currency picker — defer to polish week
            }

            SettingsUiEvent.OnThemeClicked -> {
                // Theme picker — defer to polish week
            }

            SettingsUiEvent.OnExportCSVClicked ->
                setEffect(SettingsUiEffect.ExportCSV)

            SettingsUiEvent.OnClearDataClicked -> {
                // Show confirmation dialog — handled in UI
            }

            SettingsUiEvent.OnClearDataConfirmed -> {
                // Clear all data — defer to polish week
            }
        }
    }
}