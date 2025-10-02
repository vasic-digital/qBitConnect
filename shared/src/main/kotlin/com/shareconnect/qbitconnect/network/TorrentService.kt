package com.shareconnect.qbitconnect.network

import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

class TorrentService(
    private val client: OkHttpClient,
    private val baseUrl: String
) {
    suspend fun getVersion(): Response<String> {
        return try {
            val request = Request.Builder()
                .url("$baseUrl/api/v2/app/version")
                .get()
                .build()
            val response = client.newCall(request).execute()
            Response(response.code, response.body?.string())
        } catch (e: Exception) {
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
            Response(response.code, response.body?.string())
        } catch (e: Exception) {
            Response(0, null)
        }
    }

    suspend fun getTorrents(): Response<String> {
        return try {
            val request = Request.Builder()
                .url("$baseUrl/api/v2/torrents/info")
                .get()
                .build()
            val response = client.newCall(request).execute()
            Response(response.code, response.body?.string())
        } catch (e: Exception) {
            Response(0, null)
        }
    }

    suspend fun addTorrent(urls: List<String>, savepath: String?): Response<String> {
        return try {
            val formBody = FormBody.Builder()
                .add("urls", urls.joinToString("\n"))
                .apply { if (savepath != null) add("savepath", savepath) }
                .build()
            val request = Request.Builder()
                .url("$baseUrl/api/v2/torrents/add")
                .post(formBody)
                .build()
            val response = client.newCall(request).execute()
            Response(response.code, response.body?.string())
        } catch (e: Exception) {
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
            Response(response.code, response.body?.string())
        } catch (e: Exception) {
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
            Response(response.code, response.body?.string())
        } catch (e: Exception) {
            Response(0, null)
        }
    }

    suspend fun deleteTorrents(hashes: List<String>, deleteFiles: Boolean): Response<String> {
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
            Response(response.code, response.body?.string())
        } catch (e: Exception) {
            Response(0, null)
        }
    }

    suspend fun getRSSFeeds(): Response<String> {
        return try {
            val request = Request.Builder()
                .url("$baseUrl/api/v2/rss/items")
                .get()
                .build()
            val response = client.newCall(request).execute()
            Response(response.code, response.body?.string())
        } catch (e: Exception) {
            Response(0, null)
        }
    }
}