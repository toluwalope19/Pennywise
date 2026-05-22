package com.example.domain.usecase.category

import com.example.domain.repository.CategoryRepository
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DeleteCategoryUseCaseTest {

    private lateinit var repository: CategoryRepository
    private lateinit var useCase: DeleteCategoryUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = DeleteCategoryUseCase(repository)
    }

    @Test
    fun `invoke delegates to repository with correct id`() = runTest {
        coJustRun { repository.deleteCategory(4L) }

        useCase(4L)

        coVerify(exactly = 1) { repository.deleteCategory(4L) }
    }
}
