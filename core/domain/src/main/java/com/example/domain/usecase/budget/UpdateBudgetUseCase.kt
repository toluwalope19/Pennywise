package com.example.domain.usecase.budget

import com.example.domain.model.Budget
import com.example.domain.repository.BudgetRepository
import javax.inject.Inject

class UpdateBudgetUseCase @Inject constructor(
    private val repository: BudgetRepository
) {
    suspend operator fun invoke(budget: Budget) {
        require(budget.amount > 0) {
            "Budget amount must be greater than zero"
        }
        repository.updateBudget(budget)
    }
}