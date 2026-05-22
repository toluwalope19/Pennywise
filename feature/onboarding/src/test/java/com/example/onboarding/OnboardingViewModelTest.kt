package com.example.onboarding

import app.cash.turbine.test
import com.example.domain.usecase.preference.SaveOnboardingSeenUseCase
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var saveOnboardingSeen: SaveOnboardingSeenUseCase
    private lateinit var viewModel: OnboardingViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        saveOnboardingSeen = mockk()
        viewModel = OnboardingViewModel(saveOnboardingSeen)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has isLoading false`() {
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `OnGetStarted saves onboarding and emits NavigateToDashboard`() = runTest {
        coJustRun { saveOnboardingSeen() }

        viewModel.effect.test {
            viewModel.onEvent(OnboardingUiEvent.OnGetStarted)

            val effect = awaitItem()
            assert(effect is OnboardingUiEffect.NavigateToDashboard)
        }

        coVerify(exactly = 1) { saveOnboardingSeen() }
    }

    @Test
    fun `OnGetStarted calls saveOnboardingSeen exactly once`() = runTest {
        coJustRun { saveOnboardingSeen() }

        viewModel.onEvent(OnboardingUiEvent.OnGetStarted)

        coVerify(exactly = 1) { saveOnboardingSeen() }
    }
}
