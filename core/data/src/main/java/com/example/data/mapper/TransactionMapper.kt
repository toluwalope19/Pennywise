package com.example.data.mapper

import com.example.data.local.entity.TransactionEntity
import com.example.domain.model.Transaction
import com.example.domain.model.TransactionType

fun TransactionEntity.toDomain(): Transaction =
    Transaction(
        id = id,
        amount = amount,
        type = TransactionType.valueOf(type),
        categoryId = categoryId,
        note = note,
        date = date,
        createdAt = createdAt
    )

fun Transaction.toEntity(): TransactionEntity =
    TransactionEntity(
        id = id,
        amount = amount,
        type = type.name,
        categoryId = categoryId,
        note = note,
        date = date,
        createdAt = createdAt
    )
