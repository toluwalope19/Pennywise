package com.example.data.mapper

import com.example.data.local.entity.BudgetEntity
import com.example.domain.model.Budget
import com.example.domain.model.BudgetPeriod

fun BudgetEntity.toDomain() = Budget(
    id = id,
    categoryId = categoryId,
    amount = amount,
    month = month,
    year = year,
    period = BudgetPeriod.valueOf(period),
    startDay = startDay,
    alertsEnabled = alertsEnabled,
    alertThreshold = alertThreshold
)

fun Budget.toEntity() = BudgetEntity(
    id = id,
    categoryId = categoryId,
    amount = amount,
    month = month,
    year = year,
    period = period.name,
    startDay = startDay,
    alertsEnabled = alertsEnabled,
    alertThreshold = alertThreshold
)