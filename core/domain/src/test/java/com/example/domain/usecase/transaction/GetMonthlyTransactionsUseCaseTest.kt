package com.example.domain.usecase.transaction

import com.example.domain.model.Transaction
import com.example.domain.model.TransactionType
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
import java.time.LocalDate

class GetMonthlyTransactionsUseCaseTest {

    private lateinit var repository: TransactionRepository
    private lateinit var useCase: GetMonthlyTransactionsUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetMonthlyTransactionsUseCase(repository)
    }

    @Test
    fun `invoke returns flow from repository for given month and year`() = runTest {
        val transactions = listOf(
            Transaction(1L, 100.0, TransactionType.EXPENSE, 1L, date = LocalDate.of(2024, 5, 10))
        )
        every { repository.getTransactionsByMonthList(5, 2024) } returns flowOf(transactions)

        useCase(5, 2024).test {
            assertEquals(transactions, awaitItem())
            awaitComplete()
        }

        verify(exactly = 1) { repository.getTransactionsByMonthList(5, 2024) }
    }

    @Test
    fun `invoke returns empty list when no transactions`() = runTest {
        every { repository.getTransactionsByMonthList(1, 2024) } returns flowOf(emptyList())

        useCase(1, 2024).test {
            assertEquals(emptyList<Transaction>(), awaitItem())
            awaitComplete()
        }
    }
}
