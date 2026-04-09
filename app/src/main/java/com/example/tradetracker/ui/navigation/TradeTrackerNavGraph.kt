package com.example.tradetracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tradetracker.ui.screens.AddTradeScreen
import com.example.tradetracker.ui.screens.SettingsScreen
import com.example.tradetracker.ui.screens.TradeDetailScreen
import com.example.tradetracker.ui.screens.TradeListScreen

@Composable
fun TradeTrackerNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.TradeList.route
    ) {
        composable(Screen.TradeList.route) {
            TradeListScreen(
                onTradeClick = { tradeId ->
                    navController.navigate(Screen.TradeDetail.createRoute(tradeId))
                },
                onAddTradeClick = {
                    navController.navigate(Screen.AddTrade.route)
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        composable(
            route = Screen.TradeDetail.route,
            arguments = listOf(
                navArgument("tradeId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val tradeId = backStackEntry.arguments?.getLong("tradeId") ?: 0L
            TradeDetailScreen(
                tradeId = tradeId,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.AddTrade.route) {
            AddTradeScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}