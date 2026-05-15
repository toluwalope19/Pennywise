package com.example.pennywise.navigation


sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object Dashboard : Screen("dashboard")
    data object Transactions : Screen("transactions")
    data object AddTransaction : Screen("add_transaction?type={type}") {
        fun createRoute(type: String = "EXPENSE") = "add_transaction?type=$type"
    }
    data object EditTransaction : Screen("edit_transaction/{id}") {
        fun createRoute(id: Long) = "edit_transaction/$id"
    }
    data object Budgets : Screen("budgets")
    data object Analytics : Screen("analytics")
    data object Settings : Screen("settings")
}