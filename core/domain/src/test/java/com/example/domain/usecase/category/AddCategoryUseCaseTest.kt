package com.example.domain.usecase.category

import com.example.domain.model.Category
import com.example.domain.repository.CategoryRepository
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class AddCategoryUseCaseTest {

    private lateinit var repository: CategoryRepository
    private lateinit var useCase: AddCategoryUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = AddCategoryUseCase(repository)
    }

    @Test
    fun `invoke calls repository when name is not blank`() = runTest {
        val category = Category(name = "Food", icon = "restaurant", color = "#FF5733")
        coJustRun { repository.addCategory(category) }

        useCase(category)

        coVerify(exactly = 1) { repository.addCategory(category) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke throws when name is blank`() = runTest {
        useCase(Category(name = "", icon = "icon", color = "#000000"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke throws when name is only whitespace`() = runTest {
        useCase(Category(name = "   ", icon = "icon", color = "#000000"))
    }

    @Test
    fun `error message mentions blank name`() = runTest {
        val result = runCatching {
            useCase(Category(name = "", icon = "icon", color = "#000000"))
        }
        assert(result.exceptionOrNull() is IllegalArgumentException)
        assert(result.exceptionOrNull()?.message?.contains("blank") == true)
    }

    @Test
    fun `invoke works with single character name`() = runTest {
        val category = Category(name = "A", icon = "icon", color = "#FFFFFF")
        coJustRun { repository.addCategory(category) }

        useCase(category)

        coVerify(exactly = 1) { repository.addCategory(category) }
    }
}
