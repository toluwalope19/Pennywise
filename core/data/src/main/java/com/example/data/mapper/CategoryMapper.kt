package com.example.data.mapper

import com.example.data.local.entity.CategoryEntity
import com.example.domain.model.Category

fun CategoryEntity.toDomain(): Category =
    Category(
        id = id,
        name = name,
        icon = icon,
        color = color,
        isDefault = isDefault
    )

fun Category.toEntity(): CategoryEntity =
    CategoryEntity(
        id = id,
        name = name,
        icon = icon,
        color = color,
        isDefault = isDefault
    )