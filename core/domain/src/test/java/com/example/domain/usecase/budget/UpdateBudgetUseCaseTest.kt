package com.example.domain.usecase.budget

import com.example.domain.model.Budget
import com.example.domain.model.BudgetPeriod
import com.example.domain.repository.BudgetRepository
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class UpdateBudgetUseCaseTest {

    private lateinit var repository: BudgetRepository
    private lateinit var useCase: UpdateBudgetUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = UpdateBudgetUseCase(repository)
    }

    @Test
    fun `invoke calls repository when amount is positive`() = runTest {
        val budget = budget(amount = 300.0)
        coJustRun { repository.updateBudget(budget) }

        useCase(budget)

        coVerify(exactly = 1) { repository.updateBudget(budget) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke throws when amount is zero`() = runTest {
        useCase(budget(amount = 0.0))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke throws when amount is negative`() = runTest {
        useCase(budget(amount = -100.0))
    }

    private fun budget(amount: Double) = Budget(
        id = 2L,
        categoryId = 3L,
        amount = amount,
        month = 6,
        year = 2024,
        period = BudgetPeriod.MONTHLY
    )
}
