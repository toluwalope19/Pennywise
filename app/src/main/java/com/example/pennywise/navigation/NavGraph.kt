package com.example.pennywise.navigation


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.window.core.layout.WindowSizeClass
import com.example.analytics.AnalyticsScreen
import com.example.budgets.BudgetsScreen
import com.example.dashboard.DashboardScreen
import com.example.onboarding.OnboardingScreen
import com.example.settings.SettingsScreen
import com.example.transactions.add.AddTransactionScreen
import com.example.transactions.edit.EditTransactionScreen
import com.example.transactions.edit.EditTransactionViewModel
import com.example.transactions.list.TransactionsScreen
import com.example.ui.PennywiseWindowLayout
import com.example.ui.components.PennywiseBottomNav
import com.example.ui.components.PennywiseNavigationRail
import com.example.ui.theme.Background



@Composable
fun PennywiseNavGraph(
    navController: NavHostController,
    windowLayout: PennywiseWindowLayout,
    startDestination: String = Screen.Onboarding.route
) {
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    // Replace isCompact / isExpandedOrMedium with:
    val showRail = windowLayout is PennywiseWindowLayout.TabletPortrait ||
            windowLayout is PennywiseWindowLayout.TabletLandscape ||
            windowLayout is PennywiseWindowLayout.Foldable

    val showBottomNav = windowLayout is PennywiseWindowLayout.PhonePortrait ||
            windowLayout is PennywiseWindowLayout.PhoneLandscape


    // Hide nav on these screens
    val hideNavRoutes = setOf(
        Screen.Onboarding.route,
        Screen.AddTransaction.route,
        Screen.EditTransaction.route,
        Screen.Settings.route
    )
    val showNav = currentRoute !in hideNavRoutes

    // Shared nav click handlers
    val onHomeClick = {
        navController.navigate(Screen.Dashboard.route) {
            popUpTo(Screen.Dashboard.route) { inclusive = true }
        }
    }
    val onActivityClick = {
        navController.navigate(Screen.Transactions.route) {
            popUpTo(Screen.Dashboard.route)
        }
    }
    val onAddClick = {
        navController.navigate(Screen.AddTransaction.route)
    }
    val onBudgetsClick = {
        navController.navigate(Screen.Budgets.route) {
            popUpTo(Screen.Dashboard.route)
        }
    }
    val onAnalyticsClick = {
        navController.navigate(Screen.Analytics.route) {
            popUpTo(Screen.Dashboard.route)
        }
    }
    val onAvatarClick = {
        navController.navigate(Screen.Settings.route)
    }

    Scaffold(
        containerColor = Background,
        contentWindowInsets = WindowInsets.statusBars,
        bottomBar = {
            // BottomNav — only on compact screens (phones)
            if (showBottomNav && showNav) {
                PennywiseBottomNav(
                    activeRoute = currentRoute ?: Screen.Dashboard.route,
                    onHomeClick = onHomeClick,
                    onActivityClick = onActivityClick,
                    onAddClick = onAddClick,
                    onBudgetsClick = onBudgetsClick,
                    onAnalyticsClick = onAnalyticsClick
                )
            }
        }
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // NavigationRail — only on medium/expanded screens (tablets, foldables)
            if (showRail && showNav) {
                PennywiseNavigationRail(
                    activeRoute = currentRoute ?: Screen.Dashboard.route,
                    onHomeClick = onHomeClick,
                    onActivityClick = onActivityClick,
                    onAddClick = onAddClick,
                    onBudgetsClick = onBudgetsClick,
                    onAnalyticsClick = onAnalyticsClick,
                    onAvatarClick = onAvatarClick
                )
            }

            // NavHost — fills remaining space
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
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
                            navController.navigate(
                                Screen.EditTransaction.createRoute(id)
                            )
                        },
                        onNavigateToAddTransaction = { type ->
                            navController.navigate(
                                Screen.AddTransaction.createRoute(type)
                            )
                        },
                        onNavigateToSettings = {
                            navController.navigate(Screen.Settings.route)
                        },
                        windowLayout = windowLayout
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
                        },
                        windowLayout = windowLayout
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

                composable(
                    route = Screen.EditTransaction.route,
                    arguments = listOf(
                        navArgument("id") { type = NavType.LongType }
                    )
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getLong("id") ?: 0L
                    val viewModel = hiltViewModel<EditTransactionViewModel,
                            EditTransactionViewModel.Factory> { factory ->
                        factory.create(id)
                    }

                    EditTransactionScreen(
                        onNavigateBack = { navController.popBackStack() },
                        viewModel = viewModel
                    )
                }

                composable(Screen.Budgets.route) {
                    BudgetsScreen()
                }

                composable(Screen.Analytics.route) {
                    AnalyticsScreen()
                }

                composable(Screen.Settings.route) {
                    SettingsScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

//@Composable
//fun PennywiseNavGraph(
//    navController: NavHostController,
//    windowSizeClass: WindowSizeClass,
//    startDestination: String = Screen.Onboarding.route
//) {
//    // Routes that show the bottom nav
//    val bottomNavRoutes = remember {
//        setOf(
//            Screen.Dashboard.route,
//            Screen.Transactions.route,
//            Screen.Budgets.route,
//            Screen.Analytics.route,
//            Screen.Settings.route
//        )
//    }
//
//    // Track current route
//    val currentBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentRoute = currentBackStackEntry?.destination?.route
//
//    val showBottomNav = currentRoute in bottomNavRoutes
//
//    Scaffold(
//        containerColor = Background,
//        bottomBar = {
//            if (showBottomNav) {
//                PennywiseBottomNav(
//                    activeRoute = currentRoute ?: Screen.Dashboard.route,
//                    onHomeClick = {
//                        navController.navigate(Screen.Dashboard.route) {
//                            popUpTo(Screen.Dashboard.route) { inclusive = false }
//                            launchSingleTop = true
//                        }
//                    },
//                    onActivityClick = {
//                        navController.navigate(Screen.Transactions.route) {
//                            popUpTo(Screen.Dashboard.route) { inclusive = false }
//                            launchSingleTop = true
//                        }
//                    },
//                    onAddClick = {
//                        navController.navigate(Screen.AddTransaction.createRoute())
//                    },
//                    onBudgetsClick = {
//                        navController.navigate(Screen.Budgets.route) {
//                            popUpTo(Screen.Dashboard.route) { inclusive = false }
//                            launchSingleTop = true
//                        }
//                    },
//                    onAnalyticsClick = {
//                        navController.navigate(Screen.Analytics.route) {
//                            popUpTo(Screen.Dashboard.route) { inclusive = false }
//                            launchSingleTop = true
//                        }
//                    }
//                )
//            }
//        }
//    ) { paddingValues ->
//        NavHost(
//            navController = navController,
//            startDestination = startDestination,
//            modifier = Modifier.padding(paddingValues)
//        ) {
//            composable(Screen.Onboarding.route) {
//                OnboardingScreen(
//                    onNavigateToDashboard = {
//                        navController.navigate(Screen.Dashboard.route) {
//                            popUpTo(Screen.Onboarding.route) { inclusive = true }
//                        }
//                    }
//                )
//            }
//
//            composable(Screen.Dashboard.route) {
//                DashboardScreen(
//                    onNavigateToTransactions = {
//                        navController.navigate(Screen.Transactions.route)
//                    },
//                    onNavigateToTransaction = { id ->
//                        navController.navigate(Screen.EditTransaction.createRoute(id))
//                    },
//                    onNavigateToAddTransaction = { type ->
//                        navController.navigate(Screen.AddTransaction.createRoute(type))
//                    },
//                    onNavigateToSettings = {
//                        navController.navigate(Screen.Settings.route)
//                    }
//                )
//            }
//
//            composable(Screen.Transactions.route) {
//                TransactionsScreen(
//                    onNavigateBack = { navController.popBackStack() },
//                    onNavigateToTransaction = { id ->
//                        navController.navigate(Screen.EditTransaction.createRoute(id))
//                    },
//                    onNavigateToAddTransaction = {
//                        navController.navigate(Screen.AddTransaction.createRoute())
//                    }
//                )
//            }
//
//
//            composable(
//                route = Screen.AddTransaction.route,
//                arguments = listOf(
//                    navArgument("type") {
//                        type = NavType.StringType
//                        defaultValue = "EXPENSE"
//                    }
//                )
//            ) { backStackEntry ->
//                val type = backStackEntry.arguments?.getString("type") ?: "EXPENSE"
//                AddTransactionScreen(
//                    onNavigateBack = { navController.popBackStack() },
//                    onTransactionSaved = { navController.popBackStack() }
//                )
//            }
//
//            composable(
//                route = Screen.EditTransaction.route,
//                arguments = listOf(
//                    navArgument("id") { type = NavType.LongType }
//                )
//            ) {
//                EditTransactionScreen(
//                    onNavigateBack = { navController.popBackStack() }
//                )
//            }
//
//
//            composable(Screen.Budgets.route) {
//                BudgetsScreen() // ← was commented out
//            }
//
//            composable(Screen.Analytics.route) {
//                AnalyticsScreen()
//            }
//
//            composable(Screen.Settings.route) {
//                SettingsScreen(
//                    onNavigateBack = { navController.popBackStack() }
//                )
//            }
//        }
//    }
//}
