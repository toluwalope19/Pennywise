package com.example.settings

import app.cash.turbine.test
import com.example.domain.repository.PreferencesRepository
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var preferencesRepository: PreferencesRepository
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        preferencesRepository = mockk()
        every { preferencesRepository.getHasSeenOnboarding() } returns flowOf(true)
        viewModel = SettingsViewModel(preferencesRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has correct defaults`() {
        val state = viewModel.state.value
        assertEquals("Alex Park", state.userName)
        assertTrue(state.spendingAlertsEnabled)
        assertFalse(state.weeklySummaryEnabled)
    }

    @Test
    fun `OnBackClicked emits NavigateBack effect`() = runTest {
        viewModel.effect.test {
            viewModel.onEvent(SettingsUiEvent.OnBackClicked)

            assert(awaitItem() is SettingsUiEffect.NavigateBack)
        }
    }

    @Test
    fun `OnExportCSVClicked emits ExportCSV effect`() = runTest {
        viewModel.effect.test {
            viewModel.onEvent(SettingsUiEvent.OnExportCSVClicked)

            assert(awaitItem() is SettingsUiEffect.ExportCSV)
        }
    }

    @Test
    fun `OnSpendingAlertsToggled true updates state and calls repository`() = runTest {
        coJustRun { preferencesRepository.setSpendingAlerts(true) }

        viewModel.onEvent(SettingsUiEvent.OnSpendingAlertsToggled(true))

        assertTrue(viewModel.state.value.spendingAlertsEnabled)
        coVerify(exactly = 1) { preferencesRepository.setSpendingAlerts(true) }
    }

    @Test
    fun `OnSpendingAlertsToggled false updates state and calls repository`() = runTest {
        coJustRun { preferencesRepository.setSpendingAlerts(false) }

        viewModel.onEvent(SettingsUiEvent.OnSpendingAlertsToggled(false))

        assertFalse(viewModel.state.value.spendingAlertsEnabled)
        coVerify(exactly = 1) { preferencesRepository.setSpendingAlerts(false) }
    }

    @Test
    fun `OnWeeklySummaryToggled true updates state and calls repository`() = runTest {
        coJustRun { preferencesRepository.setWeeklySummary(true) }

        viewModel.onEvent(SettingsUiEvent.OnWeeklySummaryToggled(true))

        assertTrue(viewModel.state.value.weeklySummaryEnabled)
        coVerify(exactly = 1) { preferencesRepository.setWeeklySummary(true) }
    }

    @Test
    fun `OnWeeklySummaryToggled false updates state and calls repository`() = runTest {
        coJustRun { preferencesRepository.setWeeklySummary(false) }

        viewModel.onEvent(SettingsUiEvent.OnWeeklySummaryToggled(false))

        assertFalse(viewModel.state.value.weeklySummaryEnabled)
        coVerify(exactly = 1) { preferencesRepository.setWeeklySummary(false) }
    }
}
