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


package com.shareconnect.qbitconnect.data.api

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shareconnect.qbitconnect.data.model.*
import com.shareconnect.qbitconnect.data.models.SearchPlugin
import com.shareconnect.qbitconnect.data.models.SearchResults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * qBittorrent Web API client
 * Implements comprehensive qBittorrent Web API v2
 */
class QBittorrentApiClient(
    private val serverUrl: String,
    private val username: String? = null,
    private val password: String? = null
) {
    private val tag = "QBittorrentApiClient"
    private val gson = Gson()
    private val cookieStore = ConcurrentHashMap<String, List<Cookie>>()

    private val baseUrl = serverUrl.removeSuffix("/")

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .cookieJar(object : CookieJar {
            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                cookieStore[url.host] = cookies
            }

            override fun loadForRequest(url: HttpUrl): List<Cookie> {
                return cookieStore[url.host] ?: emptyList()
            }
        })
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    /**
     * Login to qBittorrent to obtain authentication cookie
     */
    suspend fun login(): Result<Unit> = withContext(Dispatchers.IO) {
        if (username.isNullOrEmpty() || password.isNullOrEmpty()) {
            return@withContext Result.success(Unit) // No auth required
        }

        try {
            val formBody = FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build()

            val request = Request.Builder()
                .url("$baseUrl/api/v2/auth/login")
                .post(formBody)
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: ""
                when {
                    responseBody.contains("Ok.", ignoreCase = true) -> Result.success(Unit)
                    responseBody.contains("Fails.", ignoreCase = true) -> Result.failure(Exception("Invalid username or password"))
                    response.code == 200 -> Result.success(Unit) // Some versions return empty body on success
                    else -> Result.failure(Exception("Login failed: ${response.code}"))
                }
            } else {
                Result.failure(Exception("Login failed: ${response.code} ${response.message}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error logging in", e)
            Result.failure(e)
        }
    }

    /**
     * Logout from qBittorrent
     */
    suspend fun logout(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$baseUrl/api/v2/auth/logout")
                .post(FormBody.Builder().build())
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                cookieStore.clear()
                Result.success(Unit)
            } else {
                Result.failure(Exception("Logout failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error logging out", e)
            Result.failure(e)
        }
    }

    /**
     * Get qBittorrent version information
     */
    suspend fun getVersion(): Result<QBittorrentVersion> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated()

            val versionRequest = Request.Builder()
                .url("$baseUrl/api/v2/app/version")
                .get()
                .build()

            val apiVersionRequest = Request.Builder()
                .url("$baseUrl/api/v2/app/webapiVersion")
                .get()
                .build()

            val versionResponse = okHttpClient.newCall(versionRequest).execute()
            val apiVersionResponse = okHttpClient.newCall(apiVersionRequest).execute()

            if (versionResponse.isSuccessful && apiVersionResponse.isSuccessful) {
                val version = versionResponse.body?.string() ?: ""
                val apiVersion = apiVersionResponse.body?.string() ?: ""

                Result.success(
                    QBittorrentVersion(
                        version = version,
                        apiVersion = apiVersion,
                        apiVersionMin = apiVersion
                    )
                )
            } else {
                Result.failure(Exception("Get version failed: ${versionResponse.code}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting version", e)
            Result.failure(e)
        }
    }

    /**
     * Get transfer info (global statistics)
     */
    suspend fun getTransferInfo(): Result<QBittorrentTransferInfo> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated()

            val request = Request.Builder()
                .url("$baseUrl/api/v2/transfer/info")
                .get()
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    val transferInfo = gson.fromJson(responseBody, QBittorrentTransferInfo::class.java)
                    Result.success(transferInfo)
                } else {
                    Result.failure(Exception("Empty response"))
                }
            } else {
                Result.failure(Exception("Get transfer info failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting transfer info", e)
            Result.failure(e)
        }
    }

    /**
     * Get list of all torrents
     */
    suspend fun getTorrents(
        filter: String? = null,
        category: String? = null,
        tag: String? = null,
        sort: String? = null,
        reverse: Boolean? = null,
        limit: Int? = null,
        offset: Int? = null,
        hashes: List<String>? = null
    ): Result<List<QBittorrentTorrent>> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated()

            val baseHttpUrl = baseUrl.toHttpUrl()
            val urlBuilder = HttpUrl.Builder()
                .scheme(if (serverUrl.startsWith("https")) "https" else "http")
                .host(baseHttpUrl.host)
                .port(baseHttpUrl.port)
                .addPathSegments("api/v2/torrents/info")

            filter?.let { urlBuilder.addQueryParameter("filter", it) }
            category?.let { urlBuilder.addQueryParameter("category", it) }
            tag?.let { urlBuilder.addQueryParameter("tag", it) }
            sort?.let { urlBuilder.addQueryParameter("sort", it) }
            reverse?.let { urlBuilder.addQueryParameter("reverse", it.toString()) }
            limit?.let { urlBuilder.addQueryParameter("limit", it.toString()) }
            offset?.let { urlBuilder.addQueryParameter("offset", it.toString()) }
            hashes?.let { urlBuilder.addQueryParameter("hashes", it.joinToString("|")) }

            val request = Request.Builder()
                .url(urlBuilder.build())
                .get()
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    val torrents = gson.fromJson<List<QBittorrentTorrent>>(
                        responseBody,
                        object : TypeToken<List<QBittorrentTorrent>>() {}.type
                    )
                    Result.success(torrents)
                } else {
                    Result.success(emptyList())
                }
            } else {
                Result.failure(Exception("Get torrents failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting torrents", e)
            Result.failure(e)
        }
    }

    /**
     * Add torrent by URL (magnet link or torrent file URL)
     */
    suspend fun addTorrent(
        urls: String,
        options: QBittorrentAddOptions? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated()

            val formBuilder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("urls", urls)

            options?.let { opts ->
                opts.savepath?.let { formBuilder.addFormDataPart("savepath", it) }
                opts.cookie?.let { formBuilder.addFormDataPart("cookie", it) }
                opts.category?.let { formBuilder.addFormDataPart("category", it) }
                opts.tags?.let { formBuilder.addFormDataPart("tags", it) }
                opts.skipChecking?.let { formBuilder.addFormDataPart("skip_checking", it.toString()) }
                opts.paused?.let { formBuilder.addFormDataPart("paused", it.toString()) }
                opts.rootFolder?.let { formBuilder.addFormDataPart("root_folder", it.toString()) }
                opts.rename?.let { formBuilder.addFormDataPart("rename", it) }
                opts.upLimit?.let { formBuilder.addFormDataPart("upLimit", it.toString()) }
                opts.dlLimit?.let { formBuilder.addFormDataPart("dlLimit", it.toString()) }
                opts.autoTmm?.let { formBuilder.addFormDataPart("autoTMM", it.toString()) }
                opts.sequentialDownload?.let { formBuilder.addFormDataPart("sequentialDownload", it.toString()) }
                opts.firstLastPiecePrio?.let { formBuilder.addFormDataPart("firstLastPiecePrio", it.toString()) }
            }

            val request = Request.Builder()
                .url("$baseUrl/api/v2/torrents/add")
                .post(formBuilder.build())
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful || response.code == 200) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Add torrent failed: ${response.code} ${response.message}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error adding torrent", e)
            Result.failure(e)
        }
    }

    /**
     * Pause one or more torrents
     */
    suspend fun pauseTorrents(hashes: List<String>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated()

            val formBody = FormBody.Builder()
                .add("hashes", hashes.joinToString("|"))
                .build()

            val request = Request.Builder()
                .url("$baseUrl/api/v2/torrents/pause")
                .post(formBody)
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Pause torrents failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error pausing torrents", e)
            Result.failure(e)
        }
    }

    /**
     * Resume one or more torrents
     */
    suspend fun resumeTorrents(hashes: List<String>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated()

            val formBody = FormBody.Builder()
                .add("hashes", hashes.joinToString("|"))
                .build()

            val request = Request.Builder()
                .url("$baseUrl/api/v2/torrents/resume")
                .post(formBody)
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Resume torrents failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error resuming torrents", e)
            Result.failure(e)
        }
    }

    /**
     * Delete one or more torrents
     */
    suspend fun deleteTorrents(hashes: List<String>, deleteFiles: Boolean = false): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated()

            val formBody = FormBody.Builder()
                .add("hashes", hashes.joinToString("|"))
                .add("deleteFiles", deleteFiles.toString())
                .build()

            val request = Request.Builder()
                .url("$baseUrl/api/v2/torrents/delete")
                .post(formBody)
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Delete torrents failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error deleting torrents", e)
            Result.failure(e)
        }
    }

    /**
     * Recheck one or more torrents
     */
    suspend fun recheckTorrents(hashes: List<String>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated()

            val formBody = FormBody.Builder()
                .add("hashes", hashes.joinToString("|"))
                .build()

            val request = Request.Builder()
                .url("$baseUrl/api/v2/torrents/recheck")
                .post(formBody)
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Recheck torrents failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error rechecking torrents", e)
            Result.failure(e)
        }
    }

    /**
     * Set torrent category
     */
    suspend fun setCategory(hashes: List<String>, category: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated()

            val formBody = FormBody.Builder()
                .add("hashes", hashes.joinToString("|"))
                .add("category", category)
                .build()

            val request = Request.Builder()
                .url("$baseUrl/api/v2/torrents/setCategory")
                .post(formBody)
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Set category failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error setting category", e)
            Result.failure(e)
        }
    }

    /**
     * Get application preferences
     */
    suspend fun getPreferences(): Result<QBittorrentPreferences> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated()

            val request = Request.Builder()
                .url("$baseUrl/api/v2/app/preferences")
                .get()
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    val preferences = gson.fromJson(responseBody, QBittorrentPreferences::class.java)
                    Result.success(preferences)
                } else {
                    Result.failure(Exception("Empty response"))
                }
            } else {
                Result.failure(Exception("Get preferences failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting preferences", e)
            Result.failure(e)
        }
    }

    /**
     * Set application preferences
     */
    suspend fun setPreferences(preferences: Map<String, Any>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated()

            val json = gson.toJson(preferences)
            val requestBody = json.toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("$baseUrl/api/v2/app/setPreferences")
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Set preferences failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error setting preferences", e)
            Result.failure(e)
        }
    }

    /**
     * Set download speed limit
     */
    suspend fun setDownloadLimit(limit: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated()

            val formBody = FormBody.Builder()
                .add("limit", limit.toString())
                .build()

            val request = Request.Builder()
                .url("$baseUrl/api/v2/transfer/setDownloadLimit")
                .post(formBody)
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Set download limit failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error setting download limit", e)
            Result.failure(e)
        }
    }

    /**
     * Set upload speed limit
     */
    suspend fun setUploadLimit(limit: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated()

            val formBody = FormBody.Builder()
                .add("limit", limit.toString())
                .build()

            val request = Request.Builder()
                .url("$baseUrl/api/v2/transfer/setUploadLimit")
                .post(formBody)
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Set upload limit failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error setting upload limit", e)
            Result.failure(e)
        }
    }

    /**
     * Toggle alternative speed limits
     */
    suspend fun toggleAlternativeSpeedLimits(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated()

            val request = Request.Builder()
                .url("$baseUrl/api/v2/transfer/toggleSpeedLimitsMode")
                .post(FormBody.Builder().build())
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Toggle alternative speed limits failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error toggling alternative speed limits", e)
            Result.failure(e)
        }
    }

    /**
     * Create a new category
     */
    suspend fun createCategory(category: String, savePath: String = ""): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated()

            val formBody = FormBody.Builder()
                .add("category", category)
                .add("savePath", savePath)
                .build()

            val request = Request.Builder()
                .url("$baseUrl/api/v2/torrents/createCategory")
                .post(formBody)
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Create category failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error creating category", e)
            Result.failure(e)
        }
    }

    /**
     * Remove categories
     */
    suspend fun removeCategories(categories: List<String>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated()

            val formBody = FormBody.Builder()
                .add("categories", categories.joinToString("\n"))
                .build()

            val request = Request.Builder()
                .url("$baseUrl/api/v2/torrents/removeCategories")
                .post(formBody)
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Remove categories failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error removing categories", e)
            Result.failure(e)
        }
    }

    // ================================================================================
    // Search API Methods
    // ================================================================================

    /**
     * Get list of search plugins
     */
    suspend fun getSearchPlugins(): Result<List<SearchPlugin>> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated()

            val request = Request.Builder()
                .url("$baseUrl/api/v2/search/plugins")
                .get()
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    val plugins = gson.fromJson<List<SearchPlugin>>(
                        responseBody,
                        object : TypeToken<List<SearchPlugin>>() {}.type
                    )
                    Result.success(plugins)
                } else {
                    Result.failure(Exception("Empty response"))
                }
            } else {
                Result.failure(Exception("Get search plugins failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting search plugins", e)
            Result.failure(e)
        }
    }

    /**
     * Install search plugin from URL
     */
    suspend fun installSearchPlugin(sources: List<String>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated()

            val formBody = FormBody.Builder()
                .add("sources", sources.joinToString("|"))
                .build()

            val request = Request.Builder()
                .url("$baseUrl/api/v2/search/installPlugin")
                .post(formBody)
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Install search plugin failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error installing search plugin", e)
            Result.failure(e)
        }
    }

    /**
     * Uninstall search plugin
     */
    suspend fun uninstallSearchPlugin(names: List<String>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated()

            val formBody = FormBody.Builder()
                .add("names", names.joinToString("|"))
                .build()

            val request = Request.Builder()
                .url("$baseUrl/api/v2/search/uninstallPlugin")
                .post(formBody)
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Uninstall search plugin failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error uninstalling search plugin", e)
            Result.failure(e)
        }
    }

    /**
     * Enable/disable search plugin
     */
    suspend fun enableSearchPlugin(names: List<String>, enable: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated()

            val formBody = FormBody.Builder()
                .add("names", names.joinToString("|"))
                .add("enable", enable.toString())
                .build()

            val request = Request.Builder()
                .url("$baseUrl/api/v2/search/enablePlugin")
                .post(formBody)
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Enable search plugin failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error enabling search plugin", e)
            Result.failure(e)
        }
    }

    /**
     * Update search plugins
     */
    suspend fun updateSearchPlugins(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated()

            val request = Request.Builder()
                .url("$baseUrl/api/v2/search/updatePlugins")
                .post(FormBody.Builder().build())
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Update search plugins failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error updating search plugins", e)
            Result.failure(e)
        }
    }

    /**
     * Start search
     */
    suspend fun startSearch(
        pattern: String,
        plugins: List<String> = listOf("all"),
        category: String = "all"
    ): Result<Int> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated()

            val formBody = FormBody.Builder()
                .add("pattern", pattern)
                .add("plugins", plugins.joinToString("|"))
                .add("category", category)
                .build()

            val request = Request.Builder()
                .url("$baseUrl/api/v2/search/start")
                .post(formBody)
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    val json = gson.fromJson(responseBody, Map::class.java)
                    val searchId = (json["id"] as? Double)?.toInt() ?: -1
                    Result.success(searchId)
                } else {
                    Result.failure(Exception("Empty response"))
                }
            } else {
                Result.failure(Exception("Start search failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error starting search", e)
            Result.failure(e)
        }
    }

    /**
     * Stop search
     */
    suspend fun stopSearch(searchId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated()

            val formBody = FormBody.Builder()
                .add("id", searchId.toString())
                .build()

            val request = Request.Builder()
                .url("$baseUrl/api/v2/search/stop")
                .post(formBody)
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Stop search failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error stopping search", e)
            Result.failure(e)
        }
    }

    /**
     * Get search results
     */
    suspend fun getSearchResults(
        searchId: Int,
        limit: Int = 0,
        offset: Int = 0
    ): Result<SearchResults> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated()

            val urlBuilder = "$baseUrl/api/v2/search/results".toHttpUrl().newBuilder()
            urlBuilder.addQueryParameter("id", searchId.toString())
            if (limit > 0) urlBuilder.addQueryParameter("limit", limit.toString())
            if (offset > 0) urlBuilder.addQueryParameter("offset", offset.toString())

            val request = Request.Builder()
                .url(urlBuilder.build())
                .get()
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    val searchResults = gson.fromJson(responseBody, SearchResults::class.java)
                    Result.success(searchResults)
                } else {
                    Result.failure(Exception("Empty response"))
                }
            } else {
                Result.failure(Exception("Get search results failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting search results", e)
            Result.failure(e)
        }
    }

    /**
     * Delete search
     */
    suspend fun deleteSearch(searchId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            ensureAuthenticated()

            val formBody = FormBody.Builder()
                .add("id", searchId.toString())
                .build()

            val request = Request.Builder()
                .url("$baseUrl/api/v2/search/delete")
                .post(formBody)
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Delete search failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error deleting search", e)
            Result.failure(e)
        }
    }

    /**
     * Ensure authenticated (auto-login if credentials provided)
     */
    private suspend fun ensureAuthenticated() {
        if (!username.isNullOrEmpty() && !password.isNullOrEmpty() && cookieStore.isEmpty()) {
            val loginResult = login()
            if (loginResult.isFailure) {
                throw loginResult.exceptionOrNull() ?: Exception("Authentication failed")
            }
        }
    }

    companion object {
        private const val TAG = "QBittorrentApiClient"
    }
}
