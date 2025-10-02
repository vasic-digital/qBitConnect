package com.shareconnect.qbitconnect.network

import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request as OkHttpRequest
import okhttp3.Response as OkHttpResponse
import java.io.IOException

class TorrentService(
    private val client: OkHttpClient,
    private val baseUrl: String
) {
    suspend fun getVersion(): Response<String> {
        return try {
            val request = Request.Builder()
                .url("$baseUrl/api/v2/app/version")
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()
            Response(response.code, body)
        } catch (e: IOException) {
            Response(0, null)
        }
    }

    suspend fun login(username: String, password: String): Response<String> {
        return try {
            val formBody = FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build()

            val request = Request.Builder()
                .url("$baseUrl/api/v2/auth/login")
                .post(formBody)
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()
            Response(response.code, body)
        } catch (e: IOException) {
            Response(0, null)
        }
    }

    suspend fun getTorrents(): Response<String> {
        return try {
            val request = Request.Builder()
                .url("$baseUrl/api/v2/torrents/info")
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()
            Response(response.code, body)
        } catch (e: IOException) {
            Response(0, null)
        }
    }

    suspend fun addTorrent(urls: List<String>, savepath: String? = null): Response<String> {
        return try {
            val formBody = FormBody.Builder()
                .add("urls", urls.joinToString("\n"))
                .apply {
                    savepath?.let { add("savepath", it) }
                }
                .build()

            val request = Request.Builder()
                .url("$baseUrl/api/v2/torrents/add")
                .post(formBody)
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()
            Response(response.code, body)
        } catch (e: IOException) {
            Response(0, null)
        }
    }

    suspend fun pauseTorrents(hashes: List<String>): Response<String> {
        return try {
            val formBody = FormBody.Builder()
                .add("hashes", hashes.joinToString("|"))
                .build()

            val request = Request.Builder()
                .url("$baseUrl/api/v2/torrents/pause")
                .post(formBody)
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()
            Response(response.code, body)
        } catch (e: IOException) {
            Response(0, null)
        }
    }

    suspend fun resumeTorrents(hashes: List<String>): Response<String> {
        return try {
            val formBody = FormBody.Builder()
                .add("hashes", hashes.joinToString("|"))
                .build()

            val request = Request.Builder()
                .url("$baseUrl/api/v2/torrents/resume")
                .post(formBody)
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()
            Response(response.code, body)
        } catch (e: IOException) {
            Response(0, null)
        }
    }

    suspend fun deleteTorrents(hashes: List<String>, deleteFiles: Boolean = false): Response<String> {
        return try {
            val formBody = FormBody.Builder()
                .add("hashes", hashes.joinToString("|"))
                .add("deleteFiles", deleteFiles.toString())
                .build()

            val request = Request.Builder()
                .url("$baseUrl/api/v2/torrents/delete")
                .post(formBody)
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()
            Response(response.code, body)
        } catch (e: IOException) {
            Response(0, null)
        }
    }

    suspend fun search(query: String, category: String = "all", plugins: String = "enabled"): Response<String> {
        return try {
            val formBody = FormBody.Builder()
                .add("pattern", query)
                .add("category", category)
                .add("plugins", plugins)
                .build()

            val request = Request.Builder()
                .url("$baseUrl/api/v2/search/start")
                .post(formBody)
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()
            Response(response.code, body)
        } catch (e: IOException) {
            Response(0, null)
        }
    }

    suspend fun getSearchResults(id: String): Response<String> {
        return try {
            val request = Request.Builder()
                .url("$baseUrl/api/v2/search/results?id=$id")
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()
            Response(response.code, body)
        } catch (e: IOException) {
            Response(0, null)
        }
    }

    suspend fun getRSSFeeds(): Response<String> {
        return try {
            val request = Request.Builder()
                .url("$baseUrl/api/v2/rss/items")
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()
            Response(response.code, body)
        } catch (e: IOException) {
            Response(0, null)
        }
    }
}