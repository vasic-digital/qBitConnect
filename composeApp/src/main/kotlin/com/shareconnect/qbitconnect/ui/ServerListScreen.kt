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
import com.shareconnect.designsystem.compose.components.buttons.AnimatedButton
import com.shareconnect.designsystem.compose.components.buttons.ButtonSize
import com.shareconnect.designsystem.compose.components.buttons.ButtonStyle
import com.shareconnect.designsystem.compose.components.cards.AnimatedCard
import com.shareconnect.designsystem.compose.components.fabs.AnimatedFAB
import com.shareconnect.qbitconnect.data.models.Server
import com.shareconnect.qbitconnect.di.DependencyContainer
import com.shareconnect.qbitconnect.ui.viewmodels.ServerListViewModel
import com.shareconnect.qbitconnect.ui.viewmodels.ServerListViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerListScreen(navController: NavController) {
    val viewModel: ServerListViewModel = viewModel(
        factory = ServerListViewModelFactory()
    )
    val servers by viewModel.servers.collectAsState(initial = emptyList())
    val activeServer by viewModel.activeServer.collectAsState(initial = null)
    val isLoading by viewModel.isLoading.collectAsState(initial = false)
    val error by viewModel.error.collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("qBitConnect") },
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Text("⚙️")
                    }
                }
            )
        },
        floatingActionButton = {
            AnimatedFAB(onClick = { navController.navigate("add_server") }) {
                Text("+")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (servers.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No servers configured",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Add a qBittorrent server to get started",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    AnimatedButton(
                        text = "Add Server",
                        onClick = { navController.navigate("add_server") },
                        style = ButtonStyle.PRIMARY,
                        size = ButtonSize.MEDIUM
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(servers) { server ->
                        ServerItem(
                            server = server,
                            isActive = server.id == activeServer?.id,
                            onClick = { viewModel.setActiveServer(server) },
                            onNavigateToTorrents = {
                                navController.navigate("torrent_list/${server.id}")
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // Loading overlay
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Error snackbar
            error?.let {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
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
private fun ServerItem(
    server: com.shareconnect.qbitconnect.data.models.Server,
    isActive: Boolean,
    onClick: () -> Unit,
    onNavigateToTorrents: () -> Unit
) {
    AnimatedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = server.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${server.host}:${server.port}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (isActive) {
                        Text(
                            text = "Active",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                AnimatedButton(
                    text = "Torrents",
                    onClick = onNavigateToTorrents,
                    style = ButtonStyle.SECONDARY,
                    size = ButtonSize.SMALL
                )
            }
        }
    }
}