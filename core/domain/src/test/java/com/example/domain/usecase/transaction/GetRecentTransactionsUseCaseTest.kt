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

class GetRecentTransactionsUseCaseTest {

    private lateinit var repository: TransactionRepository
    private lateinit var useCase: GetRecentTransactionsUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetRecentTransactionsUseCase(repository)
    }

    @Test
    fun `invoke uses default limit of 5`() = runTest {
        every { repository.getRecentTransactions(5) } returns flowOf(emptyList())

        useCase().test { awaitItem(); awaitComplete() }

        verify(exactly = 1) { repository.getRecentTransactions(5) }
    }

    @Test
    fun `invoke uses custom limit`() = runTest {
        every { repository.getRecentTransactions(10) } returns flowOf(emptyList())

        useCase(10).test { awaitItem(); awaitComplete() }

        verify(exactly = 1) { repository.getRecentTransactions(10) }
    }

    @Test
    fun `invoke returns transactions from repository`() = runTest {
        val transactions = listOf(
            Transaction(1L, 50.0, TransactionType.EXPENSE, 1L, date = LocalDate.now()),
            Transaction(2L, 200.0, TransactionType.INCOME, 2L, date = LocalDate.now())
        )
        every { repository.getRecentTransactions(5) } returns flowOf(transactions)

        useCase().test {
            assertEquals(transactions, awaitItem())
            awaitComplete()
        }
    }
}
