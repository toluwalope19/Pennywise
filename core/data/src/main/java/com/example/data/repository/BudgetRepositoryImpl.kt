package com.example.data.repository


import com.example.data.local.dao.BudgetDao
import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.domain.model.Budget
import com.example.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BudgetRepositoryImpl @Inject constructor(
    private val dao: BudgetDao
) : BudgetRepository {

    override fun getBudgetsByMonth(month: Int, year: Int): Flow<List<Budget>> =
        dao.getBudgetsByMonth(month, year).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun getBudgetById(id: Long): Budget? {
        return dao.getBudgetById(id)?.toDomain()
    }

    override suspend fun addBudget(budget: Budget) =
        dao.insertBudget(budget.toEntity()).let { Unit }

    override suspend fun updateBudget(budget: Budget) =
        dao.updateBudget(budget.toEntity())

    override suspend fun deleteBudget(id: Long) =
        dao.deleteBudget(id)

    override suspend fun getBudgetCountForMonth(month: Int, year: Int): Int {
        return dao.getBudgetCountForMonth(month,year)
    }


}