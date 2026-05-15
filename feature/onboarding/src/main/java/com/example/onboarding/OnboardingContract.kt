package com.example.onboarding

data class OnboardingUiState(
    val isLoading: Boolean = false
)

sealed class OnboardingUiEvent {
    data object OnGetStarted : OnboardingUiEvent()
}

sealed class OnboardingUiEffect {
    data object NavigateToDashboard : OnboardingUiEffect()
}