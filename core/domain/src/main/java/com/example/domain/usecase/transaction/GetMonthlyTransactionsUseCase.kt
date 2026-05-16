package com.example.domain.usecase.transaction

import com.example.domain.model.Transaction
import com.example.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMonthlyTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(month: Int, year: Int): Flow<List<Transaction>> =
        repository.getTransactionsByMonthList(month, year)
}