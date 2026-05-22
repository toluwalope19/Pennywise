package com.example.domain.usecase.preference

import com.example.domain.repository.PreferencesRepository
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SaveOnboardingSeenUseCaseTest {

    private lateinit var repository: PreferencesRepository
    private lateinit var useCase: SaveOnboardingSeenUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = SaveOnboardingSeenUseCase(repository)
    }

    @Test
    fun `invoke calls setHasSeenOnboarding with true`() = runTest {
        coJustRun { repository.setHasSeenOnboarding(true) }

        useCase()

        coVerify(exactly = 1) { repository.setHasSeenOnboarding(true) }
    }

    @Test
    fun `invoke never calls setHasSeenOnboarding with false`() = runTest {
        coJustRun { repository.setHasSeenOnboarding(true) }

        useCase()

        coVerify(exactly = 0) { repository.setHasSeenOnboarding(false) }
    }
}
