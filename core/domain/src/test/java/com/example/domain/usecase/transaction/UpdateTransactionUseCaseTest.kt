package com.example.domain.usecase.transaction

import com.example.domain.model.Transaction
import com.example.domain.model.TransactionType
import com.example.domain.repository.TransactionRepository
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class UpdateTransactionUseCaseTest {

    private lateinit var repository: TransactionRepository
    private lateinit var useCase: UpdateTransactionUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = UpdateTransactionUseCase(repository)
    }

    @Test
    fun `invoke calls repository when amount is positive`() = runTest {
        val transaction = transaction(amount = 100.0)
        coJustRun { repository.updateTransaction(transaction) }

        useCase(transaction)

        coVerify(exactly = 1) { repository.updateTransaction(transaction) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke throws when amount is zero`() = runTest {
        useCase(transaction(amount = 0.0))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke throws when amount is negative`() = runTest {
        useCase(transaction(amount = -5.0))
    }

    @Test
    fun `invoke error message is informative`() = runTest {
        val result = runCatching { useCase(transaction(amount = 0.0)) }
        assert(result.exceptionOrNull() is IllegalArgumentException)
        assert(result.exceptionOrNull()?.message?.contains("greater than zero") == true)
    }

    private fun transaction(amount: Double) = Transaction(
        id = 1L,
        amount = amount,
        type = TransactionType.INCOME,
        categoryId = 2L,
        note = "test",
        date = LocalDate.of(2024, 6, 15)
    )
}
