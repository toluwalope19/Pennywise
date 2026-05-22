package com.example.domain.usecase.budget

import com.example.domain.model.Budget
import com.example.domain.model.BudgetPeriod
import com.example.domain.repository.BudgetRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class GetBudgetByIdUseCaseTest {

    private lateinit var repository: BudgetRepository
    private lateinit var useCase: GetBudgetByIdUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetBudgetByIdUseCase(repository)
    }

    @Test
    fun `invoke returns budget when found`() = runTest {
        val expected = Budget(
            id = 3L,
            categoryId = 1L,
            amount = 200.0,
            month = 5,
            year = 2024,
            period = BudgetPeriod.MONTHLY
        )
        coEvery { repository.getBudgetById(3L) } returns expected

        val result = useCase(3L)

        assertEquals(expected, result)
        coVerify(exactly = 1) { repository.getBudgetById(3L) }
    }

    @Test
    fun `invoke returns null when not found`() = runTest {
        coEvery { repository.getBudgetById(999L) } returns null

        val result = useCase(999L)

        assertNull(result)
    }
}
