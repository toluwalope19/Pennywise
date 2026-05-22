package com.example.domain.usecase.category

import com.example.domain.model.Category
import com.example.domain.repository.CategoryRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import app.cash.turbine.test
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetCategoriesUseCaseTest {

    private lateinit var repository: CategoryRepository
    private lateinit var useCase: GetCategoriesUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetCategoriesUseCase(repository)
    }

    @Test
    fun `invoke returns categories from repository`() = runTest {
        val categories = listOf(
            Category(1L, "Food", "restaurant", "#FF5733"),
            Category(2L, "Transport", "car", "#3498DB")
        )
        every { repository.getCategories() } returns flowOf(categories)

        useCase().test {
            assertEquals(categories, awaitItem())
            awaitComplete()
        }

        verify(exactly = 1) { repository.getCategories() }
    }

    @Test
    fun `invoke returns empty list when no categories`() = runTest {
        every { repository.getCategories() } returns flowOf(emptyList())

        useCase().test {
            assertEquals(emptyList<Category>(), awaitItem())
            awaitComplete()
        }
    }
}
