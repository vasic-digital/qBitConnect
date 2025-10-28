/*
 * Copyright (c) 2025 MeTube Share
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


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
        composable("add_server") {
            AddServerScreen(navController)
        }
        composable("torrent_list/{serverId}") { backStackEntry ->
            val serverId = backStackEntry.arguments?.getString("serverId") ?: ""
            TorrentListScreen(navController, serverId)
        }
        composable("add_torrent/{serverId}") { backStackEntry ->
            val serverId = backStackEntry.arguments?.getString("serverId") ?: ""
            val scannedUrl = backStackEntry.arguments?.getString("url")
            AddTorrentScreen(navController, serverId, scannedUrl)
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