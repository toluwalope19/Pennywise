package com.example.domain.usecase.transaction

import androidx.paging.PagingData
import com.example.domain.model.Transaction
import com.example.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(): Flow<PagingData<Transaction>> =
        repository.getTransactions()

    fun byMonth(month: Int, year: Int): Flow<PagingData<Transaction>> =
        repository.getTransactionsByMonth(month, year)
}