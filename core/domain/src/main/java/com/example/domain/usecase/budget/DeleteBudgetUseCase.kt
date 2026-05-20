package com.example.domain.usecase.budget

import com.example.domain.repository.BudgetRepository
import javax.inject.Inject

class DeleteBudgetUseCase @Inject constructor(
    private val repository: BudgetRepository
) {
    suspend operator fun invoke(id: Long) =
        repository.deleteBudget(id)
}