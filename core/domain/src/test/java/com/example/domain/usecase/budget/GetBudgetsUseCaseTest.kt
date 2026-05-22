package com.example.domain.usecase.budget

import com.example.domain.model.Budget
import com.example.domain.model.BudgetPeriod
import com.example.domain.repository.BudgetRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import app.cash.turbine.test
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetBudgetsUseCaseTest {

    private lateinit var repository: BudgetRepository
    private lateinit var useCase: GetBudgetsUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetBudgetsUseCase(repository)
    }

    @Test
    fun `invoke returns budgets for given month and year`() = runTest {
        val budgets = listOf(
            Budget(1L, 1L, 500.0, 5, 2024, BudgetPeriod.MONTHLY),
            Budget(2L, 2L, 300.0, 5, 2024, BudgetPeriod.MONTHLY)
        )
        every { repository.getBudgetsByMonth(5, 2024) } returns flowOf(budgets)

        useCase(5, 2024).test {
            assertEquals(budgets, awaitItem())
            awaitComplete()
        }

        verify(exactly = 1) { repository.getBudgetsByMonth(5, 2024) }
    }

    @Test
    fun `invoke returns empty list when no budgets`() = runTest {
        every { repository.getBudgetsByMonth(1, 2024) } returns flowOf(emptyList())

        useCase(1, 2024).test {
            assertEquals(emptyList<Budget>(), awaitItem())
            awaitComplete()
        }
    }
}
