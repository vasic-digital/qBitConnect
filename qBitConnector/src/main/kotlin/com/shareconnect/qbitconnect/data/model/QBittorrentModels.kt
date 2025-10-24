package com.shareconnect.qbitconnect.data.model

import com.google.gson.annotations.SerializedName

/**
 * qBittorrent torrent information model
 */
data class QBittorrentTorrent(
    @SerializedName("added_on")
    val addedOn: Long,

    @SerializedName("amount_left")
    val amountLeft: Long,

    @SerializedName("auto_tmm")
    val autoTmm: Boolean,

    @SerializedName("category")
    val category: String,

    @SerializedName("completed")
    val completed: Long,

    @SerializedName("completion_on")
    val completionOn: Long,

    @SerializedName("dl_limit")
    val dlLimit: Long,

    @SerializedName("dlspeed")
    val dlspeed: Long,

    @SerializedName("downloaded")
    val downloaded: Long,

    @SerializedName("downloaded_session")
    val downloadedSession: Long,

    @SerializedName("eta")
    val eta: Long,

    @SerializedName("f_l_piece_prio")
    val flPiecePrio: Boolean,

    @SerializedName("force_start")
    val forceStart: Boolean,

    @SerializedName("hash")
    val hash: String,

    @SerializedName("last_activity")
    val lastActivity: Long,

    @SerializedName("magnet_uri")
    val magnetUri: String?,

    @SerializedName("max_ratio")
    val maxRatio: Double,

    @SerializedName("max_seeding_time")
    val maxSeedingTime: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("num_complete")
    val numComplete: Int,

    @SerializedName("num_incomplete")
    val numIncomplete: Int,

    @SerializedName("num_leechs")
    val numLeechs: Int,

    @SerializedName("num_seeds")
    val numSeeds: Int,

    @SerializedName("priority")
    val priority: Int,

    @SerializedName("progress")
    val progress: Double,

    @SerializedName("ratio")
    val ratio: Double,

    @SerializedName("ratio_limit")
    val ratioLimit: Double,

    @SerializedName("save_path")
    val savePath: String,

    @SerializedName("seeding_time_limit")
    val seedingTimeLimit: Long,

    @SerializedName("seen_complete")
    val seenComplete: Long,

    @SerializedName("seq_dl")
    val seqDl: Boolean,

    @SerializedName("size")
    val size: Long,

    @SerializedName("state")
    val state: String,

    @SerializedName("super_seeding")
    val superSeeding: Boolean,

    @SerializedName("tags")
    val tags: String,

    @SerializedName("time_active")
    val timeActive: Long,

    @SerializedName("total_size")
    val totalSize: Long,

    @SerializedName("tracker")
    val tracker: String?,

    @SerializedName("up_limit")
    val upLimit: Long,

    @SerializedName("uploaded")
    val uploaded: Long,

    @SerializedName("uploaded_session")
    val uploadedSession: Long,

    @SerializedName("upspeed")
    val upspeed: Long
)

/**
 * qBittorrent preferences model
 */
data class QBittorrentPreferences(
    @SerializedName("save_path")
    val savePath: String?,

    @SerializedName("temp_path_enabled")
    val tempPathEnabled: Boolean?,

    @SerializedName("temp_path")
    val tempPath: String?,

    @SerializedName("preallocate_all")
    val preallocateAll: Boolean?,

    @SerializedName("incomplete_files_ext")
    val incompleteFilesExt: Boolean?,

    @SerializedName("auto_tmm_enabled")
    val autoTmmEnabled: Boolean?,

    @SerializedName("torrent_changed_tmm_enabled")
    val torrentChangedTmmEnabled: Boolean?,

    @SerializedName("save_path_changed_tmm_enabled")
    val savePathChangedTmmEnabled: Boolean?,

    @SerializedName("category_changed_tmm_enabled")
    val categoryChangedTmmEnabled: Boolean?,

    @SerializedName("save_resume_data_interval")
    val saveResumeDataInterval: Int?,

    @SerializedName("start_paused_enabled")
    val startPausedEnabled: Boolean?,

    @SerializedName("max_active_downloads")
    val maxActiveDownloads: Int?,

    @SerializedName("max_active_torrents")
    val maxActiveTorrents: Int?,

    @SerializedName("max_active_uploads")
    val maxActiveUploads: Int?,

    @SerializedName("dont_count_slow_torrents")
    val dontCountSlowTorrents: Boolean?,

    @SerializedName("slow_torrent_dl_rate_threshold")
    val slowTorrentDlRateThreshold: Int?,

    @SerializedName("slow_torrent_ul_rate_threshold")
    val slowTorrentUlRateThreshold: Int?,

    @SerializedName("slow_torrent_inactive_timer")
    val slowTorrentInactiveTimer: Int?,

    @SerializedName("max_ratio_enabled")
    val maxRatioEnabled: Boolean?,

    @SerializedName("max_ratio")
    val maxRatio: Double?,

    @SerializedName("max_seeding_time_enabled")
    val maxSeedingTimeEnabled: Boolean?,

    @SerializedName("max_seeding_time")
    val maxSeedingTime: Int?,

    @SerializedName("max_ratio_act")
    val maxRatioAct: Int?,

    @SerializedName("listen_port")
    val listenPort: Int?,

    @SerializedName("upnp")
    val upnp: Boolean?,

    @SerializedName("random_port")
    val randomPort: Boolean?,

    @SerializedName("dl_limit")
    val dlLimit: Long?,

    @SerializedName("up_limit")
    val upLimit: Long?,

    @SerializedName("max_connec")
    val maxConnec: Int?,

    @SerializedName("max_connec_per_torrent")
    val maxConnecPerTorrent: Int?,

    @SerializedName("max_uploads")
    val maxUploads: Int?,

    @SerializedName("max_uploads_per_torrent")
    val maxUploadsPerTorrent: Int?,

    @SerializedName("enable_utp")
    val enableUtp: Boolean?,

    @SerializedName("limit_utp_rate")
    val limitUtpRate: Boolean?,

    @SerializedName("limit_tcp_overhead")
    val limitTcpOverhead: Boolean?,

    @SerializedName("limit_lan_peers")
    val limitLanPeers: Boolean?,

    @SerializedName("alt_dl_limit")
    val altDlLimit: Long?,

    @SerializedName("alt_up_limit")
    val altUpLimit: Long?,

    @SerializedName("scheduler_enabled")
    val schedulerEnabled: Boolean?,

    @SerializedName("schedule_from_hour")
    val scheduleFromHour: Int?,

    @SerializedName("schedule_from_min")
    val scheduleFromMin: Int?,

    @SerializedName("schedule_to_hour")
    val scheduleToHour: Int?,

    @SerializedName("schedule_to_min")
    val scheduleToMin: Int?,

    @SerializedName("scheduler_days")
    val schedulerDays: Int?,

    @SerializedName("dht")
    val dht: Boolean?,

    @SerializedName("pex")
    val pex: Boolean?,

    @SerializedName("lsd")
    val lsd: Boolean?,

    @SerializedName("encryption")
    val encryption: Int?,

    @SerializedName("anonymous_mode")
    val anonymousMode: Boolean?,

    @SerializedName("queueing_enabled")
    val queueingEnabled: Boolean?,

    @SerializedName("max_active_downloads")
    val queueMaxActiveDownloads: Int?,

    @SerializedName("max_active_uploads")
    val queueMaxActiveUploads: Int?,

    @SerializedName("max_active_torrents")
    val queueMaxActiveTorrents: Int?,

    @SerializedName("dont_count_slow_torrents")
    val queueDontCountSlowTorrents: Boolean?,

    @SerializedName("slow_torrent_dl_rate_threshold")
    val queueSlowTorrentDlRateThreshold: Int?,

    @SerializedName("slow_torrent_ul_rate_threshold")
    val queueSlowTorrentUlRateThreshold: Int?,

    @SerializedName("slow_torrent_inactive_timer")
    val queueSlowTorrentInactiveTimer: Int?
)

/**
 * qBittorrent application version
 */
data class QBittorrentVersion(
    @SerializedName("version")
    val version: String,

    @SerializedName("api_version")
    val apiVersion: String,

    @SerializedName("api_version_min")
    val apiVersionMin: String
)

/**
 * qBittorrent transfer info
 */
data class QBittorrentTransferInfo(
    @SerializedName("dl_info_speed")
    val dlInfoSpeed: Long,

    @SerializedName("dl_info_data")
    val dlInfoData: Long,

    @SerializedName("up_info_speed")
    val upInfoSpeed: Long,

    @SerializedName("up_info_data")
    val upInfoData: Long,

    @SerializedName("dl_rate_limit")
    val dlRateLimit: Long,

    @SerializedName("up_rate_limit")
    val upRateLimit: Long,

    @SerializedName("dht_nodes")
    val dhtNodes: Long,

    @SerializedName("connection_status")
    val connectionStatus: String
)

/**
 * qBittorrent category
 */
data class QBittorrentCategory(
    @SerializedName("name")
    val name: String,

    @SerializedName("savePath")
    val savePath: String
)

/**
 * qBittorrent RSS feed
 */
data class QBittorrentRssFeed(
    @SerializedName("uid")
    val uid: String,

    @SerializedName("url")
    val url: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("lastBuildDate")
    val lastBuildDate: String,

    @SerializedName("isLoading")
    val isLoading: Boolean,

    @SerializedName("hasError")
    val hasError: Boolean
)

/**
 * qBittorrent download options
 */
data class QBittorrentAddOptions(
    @SerializedName("urls")
    val urls: String? = null,

    @SerializedName("torrents")
    val torrents: String? = null,

    @SerializedName("savepath")
    val savepath: String? = null,

    @SerializedName("cookie")
    val cookie: String? = null,

    @SerializedName("category")
    val category: String? = null,

    @SerializedName("tags")
    val tags: String? = null,

    @SerializedName("skip_checking")
    val skipChecking: Boolean? = false,

    @SerializedName("paused")
    val paused: Boolean? = false,

    @SerializedName("root_folder")
    val rootFolder: Boolean? = true,

    @SerializedName("rename")
    val rename: String? = null,

    @SerializedName("upLimit")
    val upLimit: Long? = null,

    @SerializedName("dlLimit")
    val dlLimit: Long? = null,

    @SerializedName("autoTMM")
    val autoTmm: Boolean? = false,

    @SerializedName("sequentialDownload")
    val sequentialDownload: Boolean? = false,

    @SerializedName("firstLastPiecePrio")
    val firstLastPiecePrio: Boolean? = false
)
