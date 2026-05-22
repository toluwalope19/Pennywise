package com.example.domain.usecase.transaction

import com.example.domain.repository.TransactionRepository
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DeleteTransactionUseCaseTest {

    private lateinit var repository: TransactionRepository
    private lateinit var useCase: DeleteTransactionUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = DeleteTransactionUseCase(repository)
    }

    @Test
    fun `invoke delegates to repository with correct id`() = runTest {
        coJustRun { repository.deleteTransaction(42L) }

        useCase(42L)

        coVerify(exactly = 1) { repository.deleteTransaction(42L) }
    }

    @Test
    fun `invoke passes zero id to repository`() = runTest {
        coJustRun { repository.deleteTransaction(0L) }

        useCase(0L)

        coVerify(exactly = 1) { repository.deleteTransaction(0L) }
    }
}
