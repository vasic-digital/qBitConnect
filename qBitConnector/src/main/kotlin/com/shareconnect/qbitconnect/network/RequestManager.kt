@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.shareconnect.qbitconnect.network

import android.util.Log
import com.shareconnect.qbitconnect.data.ServerManager
import com.shareconnect.qbitconnect.data.SettingsManager
import com.shareconnect.qbitconnect.model.QBittorrentVersion
import com.shareconnect.qbitconnect.model.RequestResult
import com.shareconnect.qbitconnect.model.ServerConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.Authenticator
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.Credentials
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response as OkHttpResponse
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import com.shareconnect.qbitconnect.network.catchRequestError

class SessionCookieJar : CookieJar {
    private val cookies = mutableMapOf<String, MutableList<Cookie>>()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val host = url.host
        this.cookies[host] = cookies.toMutableList()
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookies[url.host] ?: emptyList()
    }
}

class RequestManager(
    private val serverManager: ServerManager,
    private val settingsManager: SettingsManager,
) {
    private val torrentServiceMap = mutableMapOf<Int, TorrentService>()
    private val httpClientMap = mutableMapOf<Int, OkHttpClient>()

    private val loggedInServerIds = mutableListOf<Int>()
    private val initialLoginLocks = mutableMapOf<Int, Mutex>()

    private val versions = mutableMapOf<Int, Pair<Instant, QBittorrentVersion>>()
    private val versionLocks = mutableMapOf<Int, Mutex>()

    init {
        serverManager.addServerListener(
            add = {},
            remove = { serverConfig: ServerConfig ->
                torrentServiceMap.remove(serverConfig.id)
                httpClientMap.remove(serverConfig.id)
                loggedInServerIds.remove(serverConfig.id)
                initialLoginLocks.remove(serverConfig.id)
                versions.remove(serverConfig.id)
                versionLocks.remove(serverConfig.id)
            },
            change = { serverConfig: ServerConfig ->
                torrentServiceMap.remove(serverConfig.id)
                httpClientMap.remove(serverConfig.id)
                loggedInServerIds.remove(serverConfig.id)
                initialLoginLocks.remove(serverConfig.id)
                versions.remove(serverConfig.id)
                versionLocks.remove(serverConfig.id)
            },
        )
    }

    fun buildHttpClient(serverConfig: ServerConfig): OkHttpClient {
        val builder = OkHttpClient.Builder()

        // Add logging interceptor
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        builder.addInterceptor(logging)

        // Add user agent interceptor
        builder.addInterceptor(Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("User-Agent", "qBitConnect/1.0.0")
                .build()
            chain.proceed(request)
        })

        // Add custom headers
        builder.addInterceptor(Interceptor { chain ->
            val request = chain.request().newBuilder()
            serverConfig.advanced.customHeaders.forEach { header ->
                request.addHeader(header.key, header.value)
            }
            chain.proceed(request.build())
        })

        // Basic auth
        val basicAuth = serverConfig.advanced.basicAuth
        if (basicAuth.isEnabled && basicAuth.username != null && basicAuth.password != null) {
            builder.authenticator(object : Authenticator {
                override fun authenticate(route: Route?, response: OkHttpResponse): Request? {
                    val credential = Credentials.basic(basicAuth.username!!, basicAuth.password!!)
                    return response.request.newBuilder()
                        .header("Authorization", credential)
                        .build()
                }
            })
        }

        // Cookie jar for session management
        builder.cookieJar(SessionCookieJar())

        // Timeouts
        builder.connectTimeout(30, TimeUnit.SECONDS)
        builder.readTimeout(30, TimeUnit.SECONDS)
        builder.writeTimeout(30, TimeUnit.SECONDS)

        return builder.build()
    }

    fun getHttpClient(serverId: Int) = httpClientMap.getOrPut(serverId) {
        val serverConfig = serverManager.getServer(serverId)
        buildHttpClient(serverConfig)
    }

    fun buildTorrentService(serverConfig: ServerConfig, client: OkHttpClient) = TorrentService(
        client = client,
        baseUrl = serverConfig.requestUrl,
    )

    private fun getTorrentService(serverId: Int) = torrentServiceMap.getOrPut(serverId) {
        val serverConfig = serverManager.getServer(serverId)
        val httpClient = getHttpClient(serverId)
        buildTorrentService(serverConfig, httpClient)
    }

    private fun getInitialLoginLock(serverId: Int) = initialLoginLocks.getOrPut(serverId) { Mutex() }

    fun getQBittorrentVersion(serverId: Int) = versions[serverId]?.second ?: QBittorrentVersion.Invalid

    private suspend fun updateVersionIfNeeded(serverId: Int) {
        val versionLock = versionLocks.getOrPut(serverId) { Mutex() }
        versionLock.withLock {
            val isVersionValid = versions[serverId]?.let { (fetchDate, _) ->
                Clock.System.now() - fetchDate < 1.hours
            } == true
            if (!isVersionValid) {
                val service = getTorrentService(serverId)
                val version = service.getVersion()

                val parsedVersion = version.body?.let { versionString ->
                    QBittorrentVersion.fromString(versionString)
                } ?: QBittorrentVersion.Invalid
                versions[serverId] = Clock.System.now() to parsedVersion
            }
        }
    }

    private suspend fun tryLogin(serverId: Int): RequestResult<Unit> {
        val service = getTorrentService(serverId)
        val serverConfig = serverManager.getServer(serverId)

        return if (serverConfig.username != null && serverConfig.password != null) {
            val loginResponse = service.login(serverConfig.username!!, serverConfig.password!!)
            val code = loginResponse.code
            val body = loginResponse.body

            when {
                code == 403 -> RequestResult.Error.RequestError.Banned
                body == "Fails." || body == null -> RequestResult.Error.RequestError.InvalidCredentials
                body != "Ok." -> RequestResult.Error.RequestError.UnknownLoginResponse(body)
                else -> RequestResult.Success(Unit)
            }
        } else {
            RequestResult.Success(Unit)
        }
    }

    private suspend fun <T : Any> tryRequest(
        serverId: Int,
        block: suspend (service: TorrentService) -> Response<T>,
    ): RequestResult<T> {
        updateVersionIfNeeded(serverId)

        val service = getTorrentService(serverId)

        val blockResponse = block(service)
        val code = blockResponse.code
        val body = blockResponse.body

        return when {
            code == 200 && body != null -> RequestResult.Success(body)
            code == 403 -> RequestResult.Error.RequestError.InvalidCredentials
            else -> RequestResult.Error.ApiError(code)
        }
    }

    suspend fun <T : Any> request(serverId: Int, block: suspend (service: TorrentService) -> Response<T>) =
        catchRequestError(
            block = {
                val initialLoginLock = getInitialLoginLock(serverId)

                initialLoginLock.lock()
                if (serverId !in loggedInServerIds) {
                    val loginResponse = tryLogin(serverId)
                    if (loginResponse is RequestResult.Success) {
                        loggedInServerIds.add(serverId)
                        initialLoginLock.tryUnlock()

                        tryRequest(serverId, block)
                    } else {
                        loginResponse as RequestResult.Error
                    }
                } else {
                    initialLoginLock.tryUnlock()
                    val response = tryRequest(serverId, block)

                    if (response is RequestResult.Error.RequestError.InvalidCredentials) {
                        val loginResponse = tryLogin(serverId)

                        if (loginResponse is RequestResult.Success) {
                            tryRequest(serverId, block)
                        } else {
                            loginResponse as RequestResult.Error
                        }
                    } else {
                        response
                    }
                }
            },
            finally = {
                withContext(NonCancellable) {
                    val initialLoginLock = getInitialLoginLock(serverId)

                    if (initialLoginLock.isLocked) {
                        initialLoginLock.tryUnlock()
                    }
                }
            },
        )

    private fun Mutex.tryUnlock() {
        try {
            unlock()
        } catch (_: IllegalStateException) {
        }
    }
}