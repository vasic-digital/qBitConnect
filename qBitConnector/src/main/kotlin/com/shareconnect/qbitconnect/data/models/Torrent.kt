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