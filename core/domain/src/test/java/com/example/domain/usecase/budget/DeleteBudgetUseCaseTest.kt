package com.example.domain.usecase.budget

import com.example.domain.repository.BudgetRepository
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DeleteBudgetUseCaseTest {

    private lateinit var repository: BudgetRepository
    private lateinit var useCase: DeleteBudgetUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = DeleteBudgetUseCase(repository)
    }

    @Test
    fun `invoke delegates deletion to repository`() = runTest {
        coJustRun { repository.deleteBudget(7L) }

        useCase(7L)

        coVerify(exactly = 1) { repository.deleteBudget(7L) }
    }

    @Test
    fun `invoke works with id zero`() = runTest {
        coJustRun { repository.deleteBudget(0L) }

        useCase(0L)

        coVerify(exactly = 1) { repository.deleteBudget(0L) }
    }
}
