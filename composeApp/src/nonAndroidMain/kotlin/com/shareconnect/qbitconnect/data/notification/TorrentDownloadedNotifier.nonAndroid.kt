package com.shareconnect.qbitconnect.data.notification

import com.shareconnect.qbitconnect.model.Torrent

actual class TorrentDownloadedNotifier {
    actual fun checkCompleted(serverId: Int, torrentList: List<Torrent>) {}
    actual fun checkCompleted(serverId: Int, torrent: Torrent) {}
}
