package com.example.domain.usecase.transaction

import com.example.domain.model.Transaction
import com.example.domain.model.TransactionType
import com.example.domain.repository.TransactionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class GetTransactionByIdUseCaseTest {

    private lateinit var repository: TransactionRepository
    private lateinit var useCase: GetTransactionByIdUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetTransactionByIdUseCase(repository)
    }

    @Test
    fun `invoke returns transaction when found`() = runTest {
        val expected = Transaction(
            id = 5L,
            amount = 200.0,
            type = TransactionType.EXPENSE,
            categoryId = 1L,
            date = LocalDate.now()
        )
        coEvery { repository.getTransactionById(5L) } returns expected

        val result = useCase(5L)

        assertEquals(expected, result)
        coVerify(exactly = 1) { repository.getTransactionById(5L) }
    }

    @Test
    fun `invoke returns null when not found`() = runTest {
        coEvery { repository.getTransactionById(99L) } returns null

        val result = useCase(99L)

        assertNull(result)
    }
}
