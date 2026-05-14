package com.example.data.mapper

import com.example.data.local.entity.BudgetEntity
import com.example.domain.model.Budget

fun BudgetEntity.toDomain(): Budget =
    Budget(
        id = id,
        categoryId = categoryId,
        amount = amount,
        month = month,
        year = year
    )

fun Budget.toEntity(): BudgetEntity =
    BudgetEntity(
        id = id,
        categoryId = categoryId,
        amount = amount,
        month = month,
        year = year
    )