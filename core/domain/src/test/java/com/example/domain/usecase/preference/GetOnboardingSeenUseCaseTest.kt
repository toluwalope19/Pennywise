package com.example.domain.usecase.preference

import com.example.domain.repository.PreferencesRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import app.cash.turbine.test
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetOnboardingSeenUseCaseTest {

    private lateinit var repository: PreferencesRepository
    private lateinit var useCase: GetOnboardingSeenUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetOnboardingSeenUseCase(repository)
    }

    @Test
    fun `invoke returns true when onboarding has been seen`() = runTest {
        every { repository.getHasSeenOnboarding() } returns flowOf(true)

        useCase().test {
            assertTrue(awaitItem())
            awaitComplete()
        }

        verify(exactly = 1) { repository.getHasSeenOnboarding() }
    }

    @Test
    fun `invoke returns false when onboarding not seen`() = runTest {
        every { repository.getHasSeenOnboarding() } returns flowOf(false)

        useCase().test {
            assertFalse(awaitItem())
            awaitComplete()
        }
    }
}
