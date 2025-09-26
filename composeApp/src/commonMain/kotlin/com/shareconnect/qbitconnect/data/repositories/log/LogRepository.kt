package com.shareconnect.qbitconnect.data.repositories.log

import com.shareconnect.qbitconnect.network.RequestManager

class LogRepository(
    private val requestManager: RequestManager,
) {
    suspend fun getLog(serverId: Int) = requestManager.request(serverId) { service ->
        service.getLog()
    }
}
