package com.example.domain.repository

import com.example.domain.model.Budget
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun getBudgetsByMonth(month: Int, year: Int): Flow<List<Budget>>
    suspend fun addBudget(budget: Budget)
    suspend fun updateBudget(budget: Budget)
    suspend fun deleteBudget(id: Long)
}