package com.example.domain.usecase.transaction

import com.example.domain.model.Transaction
import com.example.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class GetTransactionsInRangeUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<Transaction>> =
        repository.getTransactionsInRange(startDate, endDate)
}