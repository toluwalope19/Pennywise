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

class GetBudgetsByMonthUseCaseTest {

    private lateinit var repository: BudgetRepository
    private lateinit var useCase: GetBudgetsByMonthUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetBudgetsByMonthUseCase(repository)
    }

    @Test
    fun `invoke delegates to repository with month and year`() = runTest {
        val budgets = listOf(Budget(1L, 1L, 400.0, 12, 2023, BudgetPeriod.MONTHLY))
        every { repository.getBudgetsByMonth(12, 2023) } returns flowOf(budgets)

        useCase(12, 2023).test {
            assertEquals(budgets, awaitItem())
            awaitComplete()
        }

        verify(exactly = 1) { repository.getBudgetsByMonth(12, 2023) }
    }

    @Test
    fun `invoke with different month returns correct flow`() = runTest {
        every { repository.getBudgetsByMonth(3, 2025) } returns flowOf(emptyList())

        useCase(3, 2025).test {
            assertEquals(emptyList<Budget>(), awaitItem())
            awaitComplete()
        }
    }
}
