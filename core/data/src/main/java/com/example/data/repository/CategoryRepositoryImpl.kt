package com.example.data.repository


import com.example.data.local.dao.CategoryDao
import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.domain.model.Category
import com.example.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val dao: CategoryDao
) : CategoryRepository {

    override fun getCategories(): Flow<List<Category>> =
        dao.getAllCategories().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun addCategory(category: Category) =
        dao.insertCategory(category.toEntity())

    override suspend fun deleteCategory(id: Long) =
        dao.deleteCategory(id)

    override suspend fun getCategoryById(id: Long): Category? =
        dao.getCategoryById(id)?.toDomain()
}