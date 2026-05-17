package com.example.onboarding

import androidx.lifecycle.viewModelScope
import com.example.common.mvi.MviViewModel
import com.example.domain.usecase.preference.SaveOnboardingSeenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val saveOnboardingSeen: SaveOnboardingSeenUseCase
) : MviViewModel<
OnboardingUiState,
OnboardingUiEvent,
OnboardingUiEffect>(
initialState = OnboardingUiState()
) {
    override fun handleEvent(event: OnboardingUiEvent) {
        when (event) {
            OnboardingUiEvent.OnGetStarted -> getStarted()
        }
    }

    private fun getStarted() {
        viewModelScope.launch {
            saveOnboardingSeen()
            setEffect(OnboardingUiEffect.NavigateToDashboard)
        }
    }
}