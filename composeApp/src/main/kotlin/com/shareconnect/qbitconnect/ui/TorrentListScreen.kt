package com.shareconnect.qbitconnect.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.shareconnect.qbitconnect.data.models.Torrent
import com.shareconnect.qbitconnect.di.DependencyContainer
import com.shareconnect.qbitconnect.ui.viewmodels.TorrentListViewModel
import com.shareconnect.qbitconnect.ui.viewmodels.TorrentListViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TorrentListScreen(navController: NavController, serverId: String) {
    val viewModel: TorrentListViewModel = viewModel(
        factory = TorrentListViewModelFactory()
    )
    val torrents by viewModel.filteredTorrents.collectAsState(initial = emptyList())
    val categories by viewModel.categories.collectAsState(initial = emptyList())
    val tags by viewModel.tags.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState(initial = false)
    val error by viewModel.error.collectAsState(initial = null)
    val searchQuery by viewModel.searchQuery.collectAsState(initial = "")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Torrents") },
                actions = {
                    IconButton(onClick = { viewModel.refreshTorrents() }) {
                        Text("🔄")
                    }
                    IconButton(onClick = { navController.navigate("add_torrent/$serverId") }) {
                        Text("+")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = { navController.navigate("rss_feeds/$serverId") }) {
                        Text("RSS")
                    }
                    TextButton(onClick = { navController.navigate("search/$serverId") }) {
                        Text("Search")
                    }
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("Servers")
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search and filters
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("Search torrents...") },
                singleLine = true
            )

            // Torrent list
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(torrents) { torrent ->
                    TorrentItem(
                        torrent = torrent,
                        onPause = { viewModel.pauseTorrents(listOf(torrent.hash)) },
                        onResume = { viewModel.resumeTorrents(listOf(torrent.hash)) },
                        onDelete = { viewModel.deleteTorrents(listOf(torrent.hash)) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Loading overlay
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            // Error snackbar
            error?.let {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(it)
                }
            }
        }
    }
}

@Composable
private fun TorrentItem(
    torrent: Torrent,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = torrent.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = torrent.progress,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${(torrent.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = torrent.state.name,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "↓ ${formatSpeed(torrent.downloadSpeed)}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "↑ ${formatSpeed(torrent.uploadSpeed)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onPause) {
                    Text("Pause")
                }
                TextButton(onClick = onResume) {
                    Text("Resume")
                }
                TextButton(onClick = onDelete) {
                    Text("Delete")
                }
            }
        }
    }
}

private fun formatSpeed(bytesPerSecond: Long): String {
    return when {
        bytesPerSecond >= 1024 * 1024 * 1024 -> "%.1f GB/s".format(bytesPerSecond / (1024.0 * 1024.0 * 1024.0))
        bytesPerSecond >= 1024 * 1024 -> "%.1f MB/s".format(bytesPerSecond / (1024.0 * 1024.0))
        bytesPerSecond >= 1024 -> "%.1f KB/s".format(bytesPerSecond / 1024.0)
        else -> "$bytesPerSecond B/s"
    }
}