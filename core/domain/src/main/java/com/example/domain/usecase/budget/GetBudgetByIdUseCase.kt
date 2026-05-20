package com.example.domain.usecase.budget

import com.example.domain.model.Budget
import com.example.domain.repository.BudgetRepository
import javax.inject.Inject

class GetBudgetByIdUseCase @Inject constructor(
    private val repository: BudgetRepository
) {
    suspend operator fun invoke(id: Long): Budget? =
        repository.getBudgetById(id)
}