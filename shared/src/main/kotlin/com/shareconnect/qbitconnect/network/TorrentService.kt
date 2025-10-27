/*
 * Copyright (c) 2025 MeTube Share
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


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