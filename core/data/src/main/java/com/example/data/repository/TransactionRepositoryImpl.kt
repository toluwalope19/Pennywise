package com.example.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.data.local.dao.TransactionDao
import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.domain.model.Transaction
import com.example.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val dao: TransactionDao
) : TransactionRepository {

    override fun getTransactions(): Flow<PagingData<Transaction>> =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { dao.getAllTransactions() }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }

    override fun getTransactionsByMonth(
        month: Int,
        year: Int
    ): Flow<PagingData<Transaction>> =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                dao.getTransactionsByMonth(month, year.toString())
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }

    override fun getRecentTransactions(limit: Int): Flow<List<Transaction>> =
        dao.getRecentTransactions(limit).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun addTransaction(transaction: Transaction) =
        dao.insertTransaction(transaction.toEntity())

    override suspend fun updateTransaction(transaction: Transaction) =
        dao.updateTransaction(transaction.toEntity())

    override suspend fun deleteTransaction(id: Long) =
        dao.deleteTransaction(id)

    override suspend fun getTransactionById(id: Long): Transaction? =
        dao.getTransactionById(id)?.toDomain()
}