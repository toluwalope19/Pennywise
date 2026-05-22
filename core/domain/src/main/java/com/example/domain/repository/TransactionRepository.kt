package com.example.domain.repository


import androidx.paging.PagingData
import com.example.domain.model.Transaction
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TransactionRepository {
    fun getTransactions(): Flow<PagingData<Transaction>>
    fun getTransactionsByMonth(month: Int, year: Int): Flow<PagingData<Transaction>>
    fun getRecentTransactions(limit: Int = 5): Flow<List<Transaction>>
    suspend fun addTransaction(transaction: Transaction)
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun deleteTransaction(id: Long)
    suspend fun getTransactionById(id: Long): Transaction?
    fun getTransactionsByMonthList(month: Int, year: Int): Flow<List<Transaction>>
    suspend fun getAllTimeIncome(): Double
    suspend fun getAllTimeExpense(): Double
    fun getAllTimeBalance(): Flow<Double>
    fun getTransactionsInRange(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<Transaction>>


}