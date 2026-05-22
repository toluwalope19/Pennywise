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

class AddBudgetUseCaseTest {

    private lateinit var repository: BudgetRepository
    private lateinit var useCase: AddBudgetUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = AddBudgetUseCase(repository)
    }

    @Test
    fun `invoke calls repository when amount is positive`() = runTest {
        val budget = budget(amount = 500.0)
        coJustRun { repository.addBudget(budget) }

        useCase(budget)

        coVerify(exactly = 1) { repository.addBudget(budget) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke throws when amount is zero`() = runTest {
        useCase(budget(amount = 0.0))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke throws when amount is negative`() = runTest {
        useCase(budget(amount = -10.0))
    }

    @Test
    fun `error message mentions budget amount`() = runTest {
        val result = runCatching { useCase(budget(amount = 0.0)) }
        assert(result.exceptionOrNull()?.message?.contains("greater than zero") == true)
    }

    private fun budget(amount: Double) = Budget(
        id = 1L,
        categoryId = 1L,
        amount = amount,
        month = 5,
        year = 2024,
        period = BudgetPeriod.MONTHLY
    )
}
