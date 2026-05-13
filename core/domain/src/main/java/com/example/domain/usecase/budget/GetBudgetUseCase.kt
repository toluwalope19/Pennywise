package com.example.domain.usecase.budget

import com.example.domain.model.Budget
import com.example.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBudgetsUseCase @Inject constructor(
    private val repository: BudgetRepository
) {
    operator fun invoke(month: Int, year: Int): Flow<List<Budget>> =
        repository.getBudgetsByMonth(month, year)
}