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

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.shareconnect.qbitconnect.di.DependencyContainer
import com.shareconnect.qbitconnect.ui.viewmodels.AddTorrentViewModel
import com.shareconnect.qbitconnect.ui.viewmodels.AddTorrentViewModelFactory
import java.net.URLDecoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTorrentScreen(navController: NavController, serverId: String, scannedUrl: String? = null) {
    val viewModel: AddTorrentViewModel = viewModel(
        factory = AddTorrentViewModelFactory()
    )

    val urls by viewModel.urls.collectAsState()
    val savePath by viewModel.savePath.collectAsState()

    // Handle scanned URL
    LaunchedEffect(scannedUrl) {
        scannedUrl?.let {
            try {
                val decodedUrl = URLDecoder.decode(it, "UTF-8")
                viewModel.setUrls(decodedUrl)
            } catch (e: Exception) {
                // Handle decoding error
            }
        }
    }
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val torrentAdded by viewModel.torrentAdded.collectAsState()

    // Navigate back when torrent is added
    if (torrentAdded) {
        navController.popBackStack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Torrent") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("←")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = urls,
                onValueChange = viewModel::updateUrls,
                label = { Text("Torrent URLs") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                placeholder = { Text("Enter magnet links or HTTP URLs (one per line)") }
            )

            OutlinedTextField(
                value = savePath,
                onValueChange = viewModel::updateSavePath,
                label = { Text("Save Path (optional)") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Leave empty for default") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.addTorrent(serverId.toIntOrNull() ?: 0) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && urls.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Add Torrent")
                }
            }

            // Error message
            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}