package com.example.domain.usecase.transaction

import com.example.domain.model.Transaction
import com.example.domain.model.TransactionType
import com.example.domain.repository.TransactionRepository
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class AddTransactionUseCaseTest {

    private lateinit var repository: TransactionRepository
    private lateinit var useCase: AddTransactionUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = AddTransactionUseCase(repository)
    }

    @Test
    fun `invoke calls repository when amount is positive`() = runTest {
        val transaction = transaction(amount = 50.0)
        coJustRun { repository.addTransaction(transaction) }

        useCase(transaction)

        coVerify(exactly = 1) { repository.addTransaction(transaction) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke throws when amount is zero`() = runTest {
        useCase(transaction(amount = 0.0))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke throws when amount is negative`() = runTest {
        useCase(transaction(amount = -1.0))
    }

    @Test
    fun `invoke error message mentions amount`() = runTest {
        val thrown = runCatching { useCase(transaction(amount = 0.0)) }
        assert(thrown.exceptionOrNull() is IllegalArgumentException)
        assert(thrown.exceptionOrNull()?.message?.contains("greater than zero") == true)
    }

    private fun transaction(amount: Double) = Transaction(
        id = 1L,
        amount = amount,
        type = TransactionType.EXPENSE,
        categoryId = 1L,
        date = LocalDate.now()
    )
}
