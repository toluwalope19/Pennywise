package com.example.domain.usecase.preference

import com.example.domain.repository.PreferencesRepository
import javax.inject.Inject

class SaveOnboardingSeenUseCase @Inject constructor(
    private val repository: PreferencesRepository
) {
    suspend operator fun invoke() =
        repository.setHasSeenOnboarding(true)
}