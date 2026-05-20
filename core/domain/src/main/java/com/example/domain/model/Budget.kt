package com.example.domain.model


data class Budget(
    val id: Long = 0,
    val categoryId: Long,
    val amount: Double,
    val month: Int,
    val year: Int,
    val period: BudgetPeriod = BudgetPeriod.MONTHLY,
    val startDay: Int = 1, // day of month the budget starts
    val alertsEnabled: Boolean = true,
    val alertThreshold: Float = 0.8f // warn at 80% by default
)

enum class BudgetPeriod {
    WEEKLY, MONTHLY, YEARLY
}