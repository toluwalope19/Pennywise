package com.example.pennywise.navigation


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.dashboard.DashboardScreen
import com.example.onboarding.OnboardingScreen
import com.example.transactions.add.AddTransactionScreen
import com.example.transactions.edit.EditTransactionScreen
import com.example.transactions.list.TransactionsScreen
import com.example.ui.components.PennywiseBottomNav
import com.example.ui.theme.Background

@Composable
fun PennywiseNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Onboarding.route
) {
    // Routes that show the bottom nav
    val bottomNavRoutes = remember {
        setOf(
            Screen.Dashboard.route,
            Screen.Transactions.route,
            Screen.Budgets.route,
            Screen.Analytics.route,
            Screen.Settings.route
        )
    }

    // Track current route
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val showBottomNav = currentRoute in bottomNavRoutes

    Scaffold(
        containerColor = Background,
        bottomBar = {
            if (showBottomNav) {
                PennywiseBottomNav(
                    activeRoute = currentRoute ?: Screen.Dashboard.route,
                    onHomeClick = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Dashboard.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    onActivityClick = {
                        navController.navigate(Screen.Transactions.route) {
                            popUpTo(Screen.Dashboard.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    onAddClick = {
                        navController.navigate(Screen.AddTransaction.createRoute())
                    },
                    onBudgetsClick = {
                        navController.navigate(Screen.Budgets.route) {
                            popUpTo(Screen.Dashboard.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    onAnalyticsClick = {
                        navController.navigate(Screen.Analytics.route) {
                            popUpTo(Screen.Dashboard.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
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
                TransactionsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToTransaction = { id ->
                        navController.navigate(Screen.EditTransaction.createRoute(id))
                    },
                    onNavigateToAddTransaction = {
                        navController.navigate(Screen.AddTransaction.createRoute())
                    }
                )
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
                AddTransactionScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onTransactionSaved = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.EditTransaction.route,
                arguments = listOf(
                    navArgument("id") { type = NavType.LongType }
                )
            ) {
                EditTransactionScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }


            composable(Screen.Budgets.route) {
                // BudgetsScreen — coming soon
            }

            composable(Screen.Analytics.route) {
                // AnalyticsScreen — coming soon
            }

            composable(Screen.Settings.route) {
                // SettingsScreen — coming soon
            }
        }
    }
}