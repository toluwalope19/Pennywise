package com.example.domain.usecase.transaction

import com.example.domain.model.Category
import com.example.domain.usecase.category.GetCategoriesUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ExportCsvUseCase @Inject constructor(
    private val getAllTransactions: GetAllTransactionsUseCase,
    private val getCategories: GetCategoriesUseCase
) {
    suspend operator fun invoke(): String {
        val transactions = getAllTransactions()

        // ← .first() gets one emission and completes — doesn't hang
        val categories = getCategories().first()
        val categoryMap = categories.associateBy { it.id }

        val sb = StringBuilder()
        sb.appendLine("Date,Type,Category,Amount,Note")

        transactions.forEach { transaction ->
            val date = transaction.date.toString()
            val type = transaction.type.name
            val category = categoryMap[transaction.categoryId]?.name ?: "Other"
            val amount = String.format("%.2f", transaction.amount)
            val note = transaction.note
                ?.replace(",", " ")
                ?.replace("\n", " ")
                ?: ""
            sb.appendLine("$date,$type,$category,$amount,$note")
        }

        return sb.toString()
    }
}