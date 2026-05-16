package com.example.pennywise.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.dashboard.DashboardScreen
import com.example.onboarding.OnboardingScreen

@Composable
fun PennywiseNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Onboarding.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToTransactions = {
                    navController.navigate(Screen.Transactions.route)
                },
                onNavigateToTransaction = { id ->
                    navController.navigate(Screen.EditTransaction.createRoute(id))
                },
                onNavigateToAddTransaction = { type ->
                    navController.navigate(Screen.AddTransaction.createRoute(type))
                }
            )
        }

        composable(Screen.Transactions.route) {
            // TransactionsScreen(navController)
        }

        composable(
            route = Screen.AddTransaction.route,
            arguments = listOf(
                navArgument("type") {
                    type = NavType.StringType
                    defaultValue = "EXPENSE"
                }
            )
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "EXPENSE"
            // AddTransactionScreen(navController, type)
        }

        composable(
            route = Screen.EditTransaction.route,
            arguments = listOf(
                navArgument("id") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            // EditTransactionScreen(navController, id)
        }

        composable(Screen.Budgets.route) {
            // BudgetsScreen(navController)
        }

        composable(Screen.Analytics.route) {
            // AnalyticsScreen(navController)
        }

        composable(Screen.Settings.route) {
            // SettingsScreen(navController)
        }
    }
}