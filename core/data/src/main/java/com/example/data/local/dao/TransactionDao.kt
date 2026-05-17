package com.example.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransaction(id: Long)

    @Query("SELECT * FROM transactions ORDER BY date DESC, createdAt DESC")
    fun getAllTransactions(): PagingSource<Int, TransactionEntity>

    @Query("""
        SELECT * FROM transactions 
        WHERE strftime('%m', date) = printf('%02d', :month)
        AND strftime('%Y', date) = :year
        ORDER BY date DESC, createdAt DESC
    """)
    fun getTransactionsByMonth(month: Int, year: String): PagingSource<Int, TransactionEntity>

    @Query("SELECT * FROM transactions ORDER BY date DESC, createdAt DESC LIMIT :limit")
    fun getRecentTransactions(limit: Int): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): TransactionEntity?

    @Query("""
    SELECT * FROM transactions 
    WHERE strftime('%m', date) = printf('%02d', :month)
    AND strftime('%Y', date) = :year
    ORDER BY date DESC, createdAt DESC
""")
    fun getTransactionsByMonthList(month: Int, year: String): Flow<List<TransactionEntity>>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'INCOME'")
    suspend fun getTotalIncome(): Double?

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE'")
    suspend fun getTotalExpense(): Double?
}