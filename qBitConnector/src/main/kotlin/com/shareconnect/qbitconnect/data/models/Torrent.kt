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


@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.shareconnect.qbitconnect.data.models

import kotlinx.datetime.Instant

data class Torrent(
    val hash: String,
    val name: String,
    val size: Long,
    val progress: Float, // 0.0 to 1.0
    val downloadSpeed: Long, // bytes per second
    val uploadSpeed: Long, // bytes per second
    val downloaded: Long,
    val uploaded: Long,
    val eta: Long, // seconds
    val state: TorrentState,
    val category: String? = null,
    val tags: List<String> = emptyList(),
    val ratio: Float,
    val addedOn: Instant,
    val completionOn: Instant? = null,
    val savePath: String,
    val contentPath: String,
    val priority: Int = 0,
    val seeds: Int = 0,
    val seedsTotal: Int = 0,
    val peers: Int = 0,
    val peersTotal: Int = 0,
    val downloadLimit: Long = -1, // -1 means unlimited
    val uploadLimit: Long = -1, // -1 means unlimited
    val timeActive: Long, // seconds
    val availability: Float
)

enum class TorrentState {
    ERROR,
    MISSING_FILES,
    UPLOADING,
    SEEDING,
    PAUSED_UP,
    QUEUED_UP,
    STALLED_UP,
    CHECKING_UP,
    FORCED_UP,
    ALLOCATING,
    DOWNLOADING,
    META_DL,
    PAUSED_DL,
    QUEUED_DL,
    STALLED_DL,
    CHECKING_DL,
    FORCED_DL,
    CHECKING_RESUME_DATA,
    MOVING,
    UNKNOWN
}