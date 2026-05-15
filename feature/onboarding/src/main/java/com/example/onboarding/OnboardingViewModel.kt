package com.example.onboarding

import com.example.common.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor() : MviViewModel<
OnboardingUiState,
OnboardingUiEvent,
OnboardingUiEffect>(
initialState = OnboardingUiState()
) {
    override fun handleEvent(event: OnboardingUiEvent) {
        when (event) {
            OnboardingUiEvent.OnGetStarted -> {
                setEffect(OnboardingUiEffect.NavigateToDashboard)
            }
        }
    }
}