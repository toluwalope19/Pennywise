package com.example.domain.usecase.transaction

import com.example.domain.repository.TransactionRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import app.cash.turbine.test
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetAllTimeBalanceUseCaseTest {

    private lateinit var repository: TransactionRepository
    private lateinit var useCase: GetAllTimeBalanceUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetAllTimeBalanceUseCase(repository)
    }

    @Test
    fun `invoke returns balance flow from repository`() = runTest {
        every { repository.getAllTimeBalance() } returns flowOf(1500.0)

        useCase().test {
            assertEquals(1500.0, awaitItem(), 0.001)
            awaitComplete()
        }

        verify(exactly = 1) { repository.getAllTimeBalance() }
    }

    @Test
    fun `invoke returns zero balance`() = runTest {
        every { repository.getAllTimeBalance() } returns flowOf(0.0)

        useCase().test {
            assertEquals(0.0, awaitItem(), 0.001)
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns negative balance`() = runTest {
        every { repository.getAllTimeBalance() } returns flowOf(-250.0)

        useCase().test {
            assertEquals(-250.0, awaitItem(), 0.001)
            awaitComplete()
        }
    }
}
