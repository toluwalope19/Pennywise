package com.example.data.seeeder


import com.example.data.local.dao.CategoryDao
import com.example.data.local.dao.TransactionDao
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.TransactionEntity
import java.time.LocalDate
import javax.inject.Inject

class DatabaseSeeder @Inject constructor(
    private val categoryDao: CategoryDao,
    private val transactionDao: TransactionDao
) {
    suspend fun seedIfNeeded() {
        val existing = categoryDao.getCategoryCount()
        if (existing > 0) return // already seeded

        val defaults = listOf(
            CategoryEntity(name = "Food", icon = "restaurant", color = "#FF8A3D", isDefault = true),
            CategoryEntity(name = "Shopping", icon = "shopping_bag", color = "#FF7AC1", isDefault = true),
            CategoryEntity(name = "Health", icon = "fitness_center", color = "#5AE9C8", isDefault = true),
            CategoryEntity(name = "Transport", icon = "directions_car", color = "#4FD1FF", isDefault = true),
            CategoryEntity(name = "Education", icon = "menu_book", color = "#B79CFF", isDefault = true),
            CategoryEntity(name = "Utilities", icon = "bolt", color = "#FFD25A", isDefault = true),
            CategoryEntity(name = "Travel", icon = "flight", color = "#5AE9C8", isDefault = true),
            CategoryEntity(name = "Income", icon = "payments", color = "#00E5A0", isDefault = true),
            CategoryEntity(name = "Other", icon = "more_horiz", color = "#8C8C8C", isDefault = true)
        )
        defaults.forEach { categoryDao.insertCategory(it) }

        seedTestTransactions()
    }

    private suspend fun seedTestTransactions() {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val monday = today.minusDays(3)

        val transactions = listOf(
            TransactionEntity(
                amount = 2400.00,
                type = "INCOME",
                categoryId = 8,
                note = "Acme Co · Salary",
                date = today,
                createdAt = System.currentTimeMillis()
            ),
            TransactionEntity(
                amount = 44.80,
                type = "EXPENSE",
                categoryId = 1,
                note = "Tartine Bakery",
                date = today,
                createdAt = System.currentTimeMillis()
            ),
            TransactionEntity(
                amount = 59.00,
                type = "EXPENSE",
                categoryId = 3,
                note = "FitLab Membership",
                date = today,
                createdAt = System.currentTimeMillis()
            ),
            TransactionEntity(
                amount = 129.50,
                type = "EXPENSE",
                categoryId = 2,
                note = "Zara",
                date = yesterday,
                createdAt = System.currentTimeMillis()
            ),
            TransactionEntity(
                amount = 18.42,
                type = "EXPENSE",
                categoryId = 4,
                note = "Uber",
                date = yesterday,
                createdAt = System.currentTimeMillis()
            ),
            TransactionEntity(
                amount = 59.00,
                type = "EXPENSE",
                categoryId = 3,
                note = "FitLab Membership",
                date = monday,
                createdAt = System.currentTimeMillis()
            ),
            TransactionEntity(
                amount = 39.00,
                type = "EXPENSE",
                categoryId = 5,
                note = "Coursera",
                date = monday,
                createdAt = System.currentTimeMillis()
            )
        )
        transactions.forEach { transactionDao.insertTransaction(it) }
    }
}

