package com.shareconnect.qbitconnect.data.repositories.torrent

import com.shareconnect.qbitconnect.network.RequestManager

class TorrentWebSeedsRepository(
    private val requestManager: RequestManager,
) {
    suspend fun getWebSeeds(serverId: Int, hash: String) = requestManager.request(serverId) { service ->
        service.getWebSeeds(hash)
    }
}
