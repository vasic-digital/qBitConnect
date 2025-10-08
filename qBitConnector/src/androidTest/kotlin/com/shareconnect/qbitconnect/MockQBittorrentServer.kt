package com.shareconnect.qbitconnect

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

class MockQBittorrentServer {
    private val server = MockWebServer()
    val url = server.url("/")

    private val torrents = mutableListOf<Map<String, Any>>()
    private val categories = mutableListOf<String>()

    init {
        server.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return when (request.path) {
                    "/api/v2/app/version" -> handleVersion()
                    "/api/v2/auth/login" -> handleLogin(request)
                    "/api/v2/torrents/info" -> handleGetTorrents()
                    "/api/v2/torrents/add" -> handleAddTorrent(request)
                    "/api/v2/torrents/pause" -> handlePauseTorrents(request)
                    "/api/v2/torrents/resume" -> handleResumeTorrents(request)
                    "/api/v2/torrents/delete" -> handleDeleteTorrents(request)
                    "/api/v2/torrents/setCategory" -> handleSetCategory(request)
                    "/api/v2/torrents/setTags" -> handleSetTags(request)
                    "/api/v2/torrents/setLimits" -> handleSetLimits(request)
                    "/api/v2/torrents/categories" -> handleGetCategories()
                    "/api/v2/torrents/createCategory" -> handleCreateCategory(request)
                    "/api/v2/torrents/deleteCategory" -> handleDeleteCategory(request)
                    "/api/v2/search/start" -> handleSearchStart(request)
                    "/api/v2/search/results" -> handleSearchResults(request)
                    "/api/v2/rss/items" -> handleGetRSS()
                    else -> MockResponse().setResponseCode(404)
                }
            }
        }

        // Add some mock categories
        categories.addAll(listOf("ISOs", "Movies", "Music", "Software"))

        // Add some mock torrents
        torrents.addAll(listOf(
            mapOf(
                "hash" to "abc123def456",
                "name" to "Ubuntu 22.04 ISO",
                "size" to 3_221_225_472L,
                "progress" to 0.75f,
                "dlspeed" to 1024000L,
                "upspeed" to 512000L,
                "state" to "downloading",
                "category" to "ISOs",
                "tags" to "linux,ubuntu",
                "ratio" to 0.5f,
                "num_seeds" to 25,
                "num_complete" to 150,
                "num_leechs" to 10,
                "num_incomplete" to 50,
                "eta" to 3600L,
                "added_on" to System.currentTimeMillis() / 1000,
                "completion_on" to 0L,
                "last_activity" to System.currentTimeMillis() / 1000
            ),
            mapOf(
                "hash" to "def456ghi789",
                "name" to "Fedora 38 ISO",
                "size" to 2_845_696_000L,
                "progress" to 1.0f,
                "dlspeed" to 0L,
                "upspeed" to 256000L,
                "state" to "seeding",
                "category" to "ISOs",
                "tags" to "linux,fedora",
                "ratio" to 1.2f,
                "num_seeds" to 0,
                "num_complete" to 200,
                "num_leechs" to 0,
                "num_incomplete" to 0,
                "eta" to 0L,
                "added_on" to (System.currentTimeMillis() / 1000) - 86400,
                "completion_on" to (System.currentTimeMillis() / 1000) - 3600,
                "last_activity" to System.currentTimeMillis() / 1000
            )
        ))
    }

    private fun handleVersion(): MockResponse {
        return MockResponse()
            .setResponseCode(200)
            .setBody("v4.5.2")
            .addHeader("Content-Type", "text/plain")
    }

    private fun handleLogin(request: RecordedRequest): MockResponse {
        val username = request.requestUrl?.queryParameter("username")
        val password = request.requestUrl?.queryParameter("password")

        return if (username == "admin" && password == "admin") {
            MockResponse()
                .setResponseCode(200)
                .setBody("Ok.")
                .addHeader("Content-Type", "text/plain")
                .addHeader("Set-Cookie", "SID=abc123; HttpOnly")
        } else {
            MockResponse()
                .setResponseCode(200)
                .setBody("Fails.")
                .addHeader("Content-Type", "text/plain")
        }
    }

    private fun handleGetTorrents(): MockResponse {
        val json = torrents.joinToString(
            prefix = "[",
            postfix = "]",
            separator = ","
        ) { torrent ->
            """
            {
                "hash": "${torrent["hash"]}",
                "name": "${torrent["name"]}",
                "size": ${torrent["size"]},
                "progress": ${torrent["progress"]},
                "dlspeed": ${torrent["dlspeed"]},
                "upspeed": ${torrent["upspeed"]},
                "state": "${torrent["state"]}",
                "category": "${torrent["category"]}",
                "tags": "${torrent["tags"]}",
                "ratio": ${torrent["ratio"]},
                "num_seeds": ${torrent["num_seeds"]},
                "num_complete": ${torrent["num_complete"]},
                "num_leechs": ${torrent["num_leechs"]},
                "num_incomplete": ${torrent["num_incomplete"]},
                "eta": ${torrent["eta"]},
                "added_on": ${torrent["added_on"]},
                "completion_on": ${torrent["completion_on"]},
                "last_activity": ${torrent["last_activity"]}
            }
            """.trimIndent()
        }

        return MockResponse()
            .setResponseCode(200)
            .setBody(json)
            .addHeader("Content-Type", "application/json")
    }

    private fun handleAddTorrent(request: RecordedRequest): MockResponse {
        // In a real implementation, you'd parse the form data and add a torrent
        return MockResponse()
            .setResponseCode(200)
            .setBody("")
            .addHeader("Content-Type", "text/plain")
    }

    private fun handlePauseTorrents(request: RecordedRequest): MockResponse {
        // Parse hashes and update torrent states
        return MockResponse()
            .setResponseCode(200)
            .setBody("")
            .addHeader("Content-Type", "text/plain")
    }

    private fun handleResumeTorrents(request: RecordedRequest): MockResponse {
        // Parse hashes and update torrent states
        return MockResponse()
            .setResponseCode(200)
            .setBody("")
            .addHeader("Content-Type", "text/plain")
    }

    private fun handleDeleteTorrents(request: RecordedRequest): MockResponse {
        // Parse hashes and remove torrents
        return MockResponse()
            .setResponseCode(200)
            .setBody("")
            .addHeader("Content-Type", "text/plain")
    }

    private fun handleSearchStart(request: RecordedRequest): MockResponse {
        // Return a search ID
        return MockResponse()
            .setResponseCode(200)
            .setBody("search_123")
            .addHeader("Content-Type", "text/plain")
    }

    private fun handleSearchResults(request: RecordedRequest): MockResponse {
        val searchId = request.requestUrl?.queryParameter("id")
        val mockResults = """
        {
            "status": "Running",
            "results": [
                {
                    "fileName": "Ubuntu 23.04 ISO",
                    "fileSize": 3221225472,
                    "fileUrl": "magnet:?xt=urn:btih:ubuntu23",
                    "nbSeeders": 50,
                    "nbLeechers": 20,
                    "siteUrl": "https://ubuntu.com",
                    "descrLink": "https://ubuntu.com/download/desktop"
                }
            ],
            "total": 1
        }
        """.trimIndent()

        return MockResponse()
            .setResponseCode(200)
            .setBody(mockResults)
            .addHeader("Content-Type", "application/json")
    }

    private fun handleGetRSS(): MockResponse {
        val mockRSS = """
        [
            {
                "title": "Ubuntu Releases",
                "url": "https://ubuntu.com/rss.xml",
                "articles": [
                    {
                        "title": "Ubuntu 23.04 Released",
                        "description": "Latest Ubuntu release",
                        "link": "https://ubuntu.com/download/desktop",
                        "torrentURL": "magnet:?xt=urn:btih:ubuntu23",
                        "date": "2023-04-20T10:00:00Z",
                        "isRead": false
                    }
                ]
            }
        ]
        """.trimIndent()

        return MockResponse()
            .setResponseCode(200)
            .setBody(mockRSS)
            .addHeader("Content-Type", "application/json")
    }

    private fun handleSetCategory(request: RecordedRequest): MockResponse {
        // Parse form data and update torrent categories
        return MockResponse()
            .setResponseCode(200)
            .setBody("")
            .addHeader("Content-Type", "text/plain")
    }

    private fun handleSetTags(request: RecordedRequest): MockResponse {
        // Parse form data and update torrent tags
        return MockResponse()
            .setResponseCode(200)
            .setBody("")
            .addHeader("Content-Type", "text/plain")
    }

    private fun handleSetLimits(request: RecordedRequest): MockResponse {
        // Parse form data and update torrent limits
        return MockResponse()
            .setResponseCode(200)
            .setBody("")
            .addHeader("Content-Type", "text/plain")
    }

    private fun handleGetCategories(): MockResponse {
        val categoriesJson = categories.joinToString(
            prefix = "{",
            postfix = "}",
            separator = ","
        ) { "\"$it\": {}" }

        return MockResponse()
            .setResponseCode(200)
            .setBody(categoriesJson)
            .addHeader("Content-Type", "application/json")
    }

    private fun handleCreateCategory(request: RecordedRequest): MockResponse {
        // Parse form data and add category
        val category = request.requestUrl?.queryParameter("category")
        if (category != null && !categories.contains(category)) {
            categories.add(category)
        }
        return MockResponse()
            .setResponseCode(200)
            .setBody("")
            .addHeader("Content-Type", "text/plain")
    }

    private fun handleDeleteCategory(request: RecordedRequest): MockResponse {
        // Parse form data and remove category
        val category = request.requestUrl?.queryParameter("category")
        categories.remove(category)
        return MockResponse()
            .setResponseCode(200)
            .setBody("")
            .addHeader("Content-Type", "text/plain")
    }

    fun shutdown() {
        server.shutdown()
    }

    fun addTorrent(torrent: Map<String, Any>) {
        torrents.add(torrent)
    }

    fun removeTorrent(hash: String) {
        torrents.removeIf { it["hash"] == hash }
    }

    fun updateTorrentState(hash: String, newState: String) {
        val index = torrents.indexOfFirst { it["hash"] == hash }
        if (index != -1) {
            torrents[index] = torrents[index].toMutableMap().apply {
                put("state", newState)
            }
        }
    }
}