package com.shareconnect.qbitconnect.data.repositories.search

import com.shareconnect.qbitconnect.network.RequestManager

class SearchStartRepository(
    private val requestManager: RequestManager,
) {
    suspend fun getPlugins(serverId: Int) = requestManager.request(serverId) { service ->
        service.getPlugins()
    }
}
