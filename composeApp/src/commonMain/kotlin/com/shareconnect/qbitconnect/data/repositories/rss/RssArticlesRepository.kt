package com.shareconnect.qbitconnect.data.repositories.rss

import com.shareconnect.qbitconnect.network.RequestManager

class RssArticlesRepository(
    private val requestManager: RequestManager,
) {
    suspend fun getRssFeedsWithData(serverId: Int, path: List<String>, uid: String?) =
        requestManager.request(serverId) { service ->
            service.getRssFeedWithData(path, uid)
        }

    suspend fun markAsRead(serverId: Int, feedPath: List<String>, articleId: String?) =
        requestManager.request(serverId) { service ->
            service.markAsRead(feedPath.joinToString("\\"), articleId)
        }

    suspend fun refreshItem(serverId: Int, feedPath: List<String>) = requestManager.request(serverId) { service ->
        service.refreshItem(feedPath.joinToString("\\"))
    }
}
