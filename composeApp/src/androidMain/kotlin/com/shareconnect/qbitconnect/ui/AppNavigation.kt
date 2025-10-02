package com.shareconnect.qbitconnect.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "server_list") {
        composable("server_list") {
            ServerListScreen(navController)
        }
        composable("torrent_list/{serverId}") { backStackEntry ->
            val serverId = backStackEntry.arguments?.getString("serverId") ?: ""
            TorrentListScreen(navController, serverId)
        }
        composable("add_torrent/{serverId}") { backStackEntry ->
            val serverId = backStackEntry.arguments?.getString("serverId") ?: ""
            AddTorrentScreen(navController, serverId)
        }
        composable("rss_feeds/{serverId}") { backStackEntry ->
            val serverId = backStackEntry.arguments?.getString("serverId") ?: ""
            RSSFeedsScreen(navController, serverId)
        }
        composable("search/{serverId}") { backStackEntry ->
            val serverId = backStackEntry.arguments?.getString("serverId") ?: ""
            SearchScreen(navController, serverId)
        }
        composable("settings") {
            SettingsScreen(navController)
        }
    }
}