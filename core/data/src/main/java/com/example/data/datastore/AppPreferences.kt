package com.example.data.datastore

data class AppPreferences(
    val currencySymbol: String = "₦",
    val currencyCode: String = "NGN",
    val isDarkTheme: Boolean = true,
    val spendingAlertsEnabled: Boolean = true,
    val weeklySummaryEnabled: Boolean = false
)
