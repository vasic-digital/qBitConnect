package com.shareconnect.qbitconnect.network

import android.util.Log
import com.shareconnect.qbitconnect.data.ServerManager
import com.shareconnect.qbitconnect.data.SettingsManager
import com.shareconnect.qbitconnect.data.models.QBittorrentVersion
import com.shareconnect.qbitconnect.data.models.ServerConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.Authenticator
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

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
        // Listen for server changes to clean up resources
        CoroutineScope(Dispatchers.Default).launch {
            serverManager.serversFlow.collectLatest { servers ->
                val currentIds = servers.map { it.id }.toSet()
                val previousIds = torrentServiceMap.keys + httpClientMap.keys

                // Remove resources for deleted servers
                previousIds.filter { it !in currentIds }.forEach { serverId ->
                    torrentServiceMap.remove(serverId)
                    httpClientMap.remove(serverId)
                    loggedInServerIds.remove(serverId)
                    initialLoginLocks.remove(serverId)
                    versions.remove(serverId)
                    versionLocks.remove(serverId)
                }
            }
        }
    }

    suspend fun buildHttpClient(serverConfig: ServerConfig) = OkHttpClient.Builder().apply {
        // Set timeouts from settings
        val timeout = settingsManager.connectionTimeout.first()
        readTimeout(timeout.toLong(), TimeUnit.SECONDS)
        writeTimeout(timeout.toLong(), TimeUnit.SECONDS)
        connectTimeout(timeout.toLong(), TimeUnit.SECONDS)

        // Add cookie jar
        val cookieManager = CookieManager().apply {
            setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        }
        cookieJar(JavaNetCookieJar(cookieManager))

        // Add user agent
        addInterceptor(Interceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", "qBitConnect/1.0.0")
                .build()
            chain.proceed(request)
        })

        // Add logging interceptor for debug builds
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        addInterceptor(logging)

        // Add basic auth if configured
        serverConfig.username?.let { username ->
            serverConfig.password?.let { password ->
                authenticator(object : Authenticator {
                    override fun authenticate(route: Route?, response: OkHttpResponse): OkHttpRequest? {
                        if (response.request.header("Authorization") != null) {
                            return null // Give up, we've already attempted to authenticate.
                        }
                        return response.request.newBuilder()
                            .header("Authorization", Credentials.basic(username, password))
                            .build()
                    }
                })
            }
        }

        // Add custom headers if any (placeholder for future implementation)
        // server.customHeaders?.forEach { (key, value) ->
        //     addInterceptor(Interceptor { chain ->
        //         val request = chain.request().newBuilder()
        //             .header(key, value)
        //             .build()
        //         chain.proceed(request)
        //     })
        // }
    }.build()

    suspend fun getHttpClient(serverId: Int) = httpClientMap.getOrPut(serverId) {
        val serverConfig = serverManager.getServerOrNull(serverId) ?: throw IllegalArgumentException("Server not found: $serverId")
        buildHttpClient(serverConfig)
    }

    fun buildTorrentService(serverConfig: ServerConfig, client: OkHttpClient) = TorrentService(
        client = client,
        baseUrl = serverConfig.requestUrl,
    )

    private suspend fun getTorrentService(serverId: Int) = torrentServiceMap.getOrPut(serverId) {
        val serverConfig = serverManager.getServerOrNull(serverId) ?: throw IllegalArgumentException("Server not found: $serverId")
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

                val parsedVersion = version.body?.let { QBittorrentVersion.fromString(it) } ?: QBittorrentVersion.Invalid
                versions[serverId] = Clock.System.now() to parsedVersion
            }
        }
    }

    private suspend fun tryLogin(serverId: Int): RequestResult<Unit> {
        val service = getTorrentService(serverId)
        val serverConfig = serverManager.getServerOrNull(serverId) ?: return RequestResult.Error.NetworkError(IllegalArgumentException("Server not found"))

        return if (serverConfig.username != null && serverConfig.password != null) {
            val loginResponse = service.login(serverConfig.username, serverConfig.password)
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

    private suspend fun tryRequest(
        serverId: Int,
        block: suspend (service: TorrentService) -> Response<String>,
    ): RequestResult<String> {
        updateVersionIfNeeded(serverId)

        val service = getTorrentService(serverId)

        val blockResponse = block(service)
        val code = blockResponse.code
        val body = blockResponse.body

        return when (code) {
            200 if body != null -> RequestResult.Success(body)
            403 -> RequestResult.Error.RequestError.InvalidCredentials
            else -> RequestResult.Error.ApiError(code)
        }
    }

    suspend fun request(serverId: Int, block: suspend (service: TorrentService) -> Response<String>) =
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