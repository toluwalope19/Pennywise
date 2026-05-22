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

class GetTransactionsInRangeUseCaseTest {

    private lateinit var repository: TransactionRepository
    private lateinit var useCase: GetTransactionsInRangeUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetTransactionsInRangeUseCase(repository)
    }

    @Test
    fun `invoke delegates to repository with correct date range`() = runTest {
        val start = LocalDate.of(2024, 1, 1)
        val end = LocalDate.of(2024, 6, 30)
        val transactions = listOf(
            Transaction(1L, 100.0, TransactionType.EXPENSE, 1L, date = LocalDate.of(2024, 3, 15))
        )
        every { repository.getTransactionsInRange(start, end) } returns flowOf(transactions)

        useCase(start, end).test {
            assertEquals(transactions, awaitItem())
            awaitComplete()
        }

        verify(exactly = 1) { repository.getTransactionsInRange(start, end) }
    }

    @Test
    fun `invoke returns empty list for range with no transactions`() = runTest {
        val start = LocalDate.of(2020, 1, 1)
        val end = LocalDate.of(2020, 1, 31)
        every { repository.getTransactionsInRange(start, end) } returns flowOf(emptyList())

        useCase(start, end).test {
            assertEquals(emptyList<Transaction>(), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke works for single-day range`() = runTest {
        val date = LocalDate.of(2024, 5, 10)
        every { repository.getTransactionsInRange(date, date) } returns flowOf(emptyList())

        useCase(date, date).test { awaitItem(); awaitComplete() }

        verify { repository.getTransactionsInRange(date, date) }
    }
}
