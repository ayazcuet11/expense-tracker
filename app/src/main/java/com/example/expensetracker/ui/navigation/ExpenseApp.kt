package com.example.expensetracker.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.expensetracker.ui.components.BottomBar
import com.example.expensetracker.ui.screens.activity.ActivityScreen
import com.example.expensetracker.ui.screens.add.AddTransactionScreen
import com.example.expensetracker.ui.screens.home.HomeScreen
import com.example.expensetracker.ui.screens.insights.InsightsScreen
import com.example.expensetracker.ui.screens.stats.StatsScreen

/** Root composable: hosts the bottom-nav scaffold and the navigation graph. */
@Composable
fun ExpenseTrackerApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val showBottomBar = currentRoute in Routes.mainRoutes

    fun switchTab(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                BottomBar(
                    currentRoute = currentRoute,
                    onNavigate = ::switchTab,
                    onAdd = { navController.navigate(Routes.add()) }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    onTransactionClick = { id -> navController.navigate(Routes.add(id)) },
                    onSeeStats = { switchTab(Routes.STATS) },
                    onSeeActivity = { switchTab(Routes.ACTIVITY) },
                    onSeeInsights = { switchTab(Routes.INSIGHTS) }
                )
            }
            composable(Routes.ACTIVITY) {
                ActivityScreen(
                    onTransactionClick = { id -> navController.navigate(Routes.add(id)) }
                )
            }
            composable(Routes.STATS) {
                StatsScreen()
            }
            composable(Routes.INSIGHTS) {
                InsightsScreen()
            }
            composable(
                route = Routes.ADD_WITH_ID,
                arguments = listOf(
                    navArgument("id") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) {
                AddTransactionScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
