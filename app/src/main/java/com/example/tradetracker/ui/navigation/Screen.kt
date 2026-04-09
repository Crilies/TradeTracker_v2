package com.example.tradetracker.ui.navigation

sealed class Screen(val route: String) {
    object TradeList : Screen("trade_list")
    object TradeDetail : Screen("trade_detail/{tradeId}") {
        fun createRoute(tradeId: Long) = "trade_detail/$tradeId"
    }
    object AddTrade : Screen("add_trade")
    object Settings : Screen("settings")
}