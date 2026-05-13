package com.example.domain.repository

import com.example.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getCategories(): Flow<List<Category>>
    suspend fun addCategory(category: Category)
    suspend fun deleteCategory(id: Long)
    suspend fun getCategoryById(id: Long): Category?
}