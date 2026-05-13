package com.example.domain.usecase.transaction

import com.example.domain.repository.TransactionRepository
import javax.inject.Inject


class DeleteTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(id: Long) =
        repository.deleteTransaction(id)
}