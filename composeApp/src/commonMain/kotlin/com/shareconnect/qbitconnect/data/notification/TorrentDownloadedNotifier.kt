package com.shareconnect.qbitconnect.data.notification

import com.shareconnect.qbitconnect.model.Torrent

expect class TorrentDownloadedNotifier() {
    fun checkCompleted(serverId: Int, torrentList: List<Torrent>)
    fun checkCompleted(serverId: Int, torrent: Torrent)
}
