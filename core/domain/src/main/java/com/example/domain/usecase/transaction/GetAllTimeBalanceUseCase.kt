package com.example.domain.usecase.transaction

import com.example.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllTimeBalanceUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(): Flow<Double> =
        repository.getAllTimeBalance()
}