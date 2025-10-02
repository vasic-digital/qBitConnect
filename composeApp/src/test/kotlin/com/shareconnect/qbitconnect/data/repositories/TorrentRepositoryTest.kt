package com.shareconnect.qbitconnect.data.repositories

import com.shareconnect.qbitconnect.data.models.AddTorrentRequest
import com.shareconnect.qbitconnect.data.models.Server
import com.shareconnect.qbitconnect.network.RequestManager
import com.shareconnect.qbitconnect.network.RequestResult
import com.shareconnect.qbitconnect.network.Response
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TorrentRepositoryTest {

    private lateinit var requestManager: RequestManager
    private lateinit var torrentRepository: TorrentRepository
    private lateinit var mockServer: Server

    @Before
    fun setUp() {
        requestManager = mockk()
        torrentRepository = TorrentRepository(requestManager)
        mockServer = Server(
            id = "test-server",
            name = "Test Server",
            host = "localhost",
            port = 8080,
            username = "admin",
            password = "admin"
        )
    }

    @Test
    fun `refreshTorrents should return success when API call succeeds`() = runTest {
        // Given
        val mockResponse = """
        [
            {
                "hash": "abc123def456",
                "name": "Ubuntu 22.04 ISO",
                "size": 3221225472,
                "progress": 0.75,
                "dlspeed": 1024000,
                "upspeed": 512000,
                "state": "downloading",
                "category": "ISOs",
                "tags": "linux,ubuntu",
                "ratio": 0.5,
                "num_seeds": 25,
                "num_complete": 150,
                "num_leechs": 10,
                "num_incomplete": 50,
                "eta": 3600,
                "added_on": ${System.currentTimeMillis() / 1000},
                "completion_on": 0,
                "dl_limit": -1,
                "up_limit": -1,
                "save_path": "/downloads",
                "content_path": "/downloads/ubuntu.iso",
                "priority": 0,
                "time_active": 1800,
                "availability": 0.8
            }
        ]
        """.trimIndent()

        coEvery {
            requestManager.request<String>(mockServer.id, any())
        } returns RequestResult.Success(mockResponse)

        // When
        val result = torrentRepository.refreshTorrents(mockServer)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, torrentRepository.torrents.value.size)
        assertEquals("Ubuntu 22.04 ISO", torrentRepository.torrents.value[0].name)
    }

    @Test
    fun `refreshTorrents should return failure when API call fails`() = runTest {
        // Given
        coEvery {
            requestManager.request<String>(mockServer.id, any())
        } returns RequestResult.Error.NetworkError(Exception("Network error"))

        // When
        val result = torrentRepository.refreshTorrents(mockServer)

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `addTorrent should return success when API call succeeds`() = runTest {
        // Given
        val request = AddTorrentRequest(urls = listOf("magnet:?xt=urn:btih:test"))
        coEvery {
            requestManager.request<String>(mockServer.id, any())
        } returns RequestResult.Success("")

        // When
        val result = torrentRepository.addTorrent(mockServer, request)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `pauseTorrents should return success when API call succeeds`() = runTest {
        // Given
        val hashes = listOf("abc123", "def456")
        coEvery {
            requestManager.request<String>(mockServer.id, any())
        } returns RequestResult.Success("")

        // When
        val result = torrentRepository.pauseTorrents(mockServer, hashes)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `resumeTorrents should return success when API call succeeds`() = runTest {
        // Given
        val hashes = listOf("abc123", "def456")
        coEvery {
            requestManager.request<String>(mockServer.id, any())
        } returns RequestResult.Success("")

        // When
        val result = torrentRepository.resumeTorrents(mockServer, hashes)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `deleteTorrents should return success when API call succeeds`() = runTest {
        // Given
        val hashes = listOf("abc123", "def456")
        coEvery {
            requestManager.request<String>(mockServer.id, any())
        } returns RequestResult.Success("")

        // When
        val result = torrentRepository.deleteTorrents(mockServer, hashes, deleteFiles = true)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `setCategory should return success when API call succeeds`() = runTest {
        // Given
        val hashes = listOf("abc123", "def456")
        val category = "Movies"
        coEvery {
            requestManager.request<String>(mockServer.id, any())
        } returns RequestResult.Success("")

        // When
        val result = torrentRepository.setCategory(mockServer, hashes, category)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `setTags should return success when API call succeeds`() = runTest {
        // Given
        val hashes = listOf("abc123", "def456")
        val tags = listOf("action", "hd")
        coEvery {
            requestManager.request<String>(mockServer.id, any())
        } returns RequestResult.Success("")

        // When
        val result = torrentRepository.setTags(mockServer, hashes, tags)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `setLimits should return success when API call succeeds`() = runTest {
        // Given
        val hashes = listOf("abc123", "def456")
        val downloadLimit = 1024000L
        val uploadLimit = 512000L
        coEvery {
            requestManager.request<String>(mockServer.id, any())
        } returns RequestResult.Success("")

        // When
        val result = torrentRepository.setLimits(mockServer, hashes, downloadLimit, uploadLimit)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `getCategories should return success when API call succeeds`() = runTest {
        // Given
        val mockResponse = """{"ISOs": {}, "Movies": {}, "Music": {}}"""
        coEvery {
            requestManager.request<String>(mockServer.id, any())
        } returns RequestResult.Success(mockResponse)

        // When
        val result = torrentRepository.getCategories(mockServer)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(listOf("ISOs", "Movies", "Music"), result.getOrNull())
    }

    @Test
    fun `createCategory should return success when API call succeeds`() = runTest {
        // Given
        val category = "NewCategory"
        val savePath = "/downloads/new"
        coEvery {
            requestManager.request<String>(mockServer.id, any())
        } returns RequestResult.Success("")

        // When
        val result = torrentRepository.createCategory(mockServer, category, savePath)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `deleteCategory should return success when API call succeeds`() = runTest {
        // Given
        val category = "OldCategory"
        coEvery {
            requestManager.request<String>(mockServer.id, any())
        } returns RequestResult.Success("")

        // When
        val result = torrentRepository.deleteCategory(mockServer, category)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `getTorrentByHash should return correct torrent when exists`() = runTest {
        // Given
        val mockResponse = """
        [
            {
                "hash": "abc123def456",
                "name": "Test Torrent",
                "size": 1000000,
                "progress": 0.5,
                "dlspeed": 1000,
                "upspeed": 500,
                "state": "downloading",
                "category": "",
                "tags": "",
                "ratio": 0.1,
                "num_seeds": 10,
                "num_complete": 100,
                "num_leechs": 5,
                "num_incomplete": 20,
                "eta": 1000,
                "added_on": ${System.currentTimeMillis() / 1000},
                "completion_on": 0,
                "dl_limit": -1,
                "up_limit": -1,
                "save_path": "/downloads",
                "content_path": "/downloads/test.torrent",
                "priority": 0,
                "time_active": 500,
                "availability": 0.9
            }
        ]
        """.trimIndent()

        coEvery {
            requestManager.request<String>(mockServer.id, any())
        } returns RequestResult.Success(mockResponse)

        torrentRepository.refreshTorrents(mockServer)

        // When
        val torrent = torrentRepository.getTorrentByHash("abc123def456")

        // Then
        assertEquals("Test Torrent", torrent?.name)
    }

    @Test
    fun `getTorrentByHash should return null when torrent doesn't exist`() = runTest {
        // When
        val torrent = torrentRepository.getTorrentByHash("nonexistent")

        // Then
        assertEquals(null, torrent)
    }

    @Test
    fun `getTorrentsByCategory should return correct torrents`() = runTest {
        // Given
        val mockResponse = """
        [
            {
                "hash": "abc123",
                "name": "Movie 1",
                "size": 1000000,
                "progress": 1.0,
                "dlspeed": 0,
                "upspeed": 1000,
                "state": "seeding",
                "category": "Movies",
                "tags": "",
                "ratio": 1.0,
                "num_seeds": 0,
                "num_complete": 100,
                "num_leechs": 0,
                "num_incomplete": 0,
                "eta": 0,
                "added_on": ${System.currentTimeMillis() / 1000},
                "completion_on": ${(System.currentTimeMillis() / 1000) - 3600},
                "dl_limit": -1,
                "up_limit": -1,
                "save_path": "/downloads",
                "content_path": "/downloads/movie1.mp4",
                "priority": 0,
                "time_active": 3600,
                "availability": 1.0
            },
            {
                "hash": "def456",
                "name": "ISO 1",
                "size": 5000000,
                "progress": 0.8,
                "dlspeed": 2000,
                "upspeed": 0,
                "state": "downloading",
                "category": "ISOs",
                "tags": "",
                "ratio": 0.0,
                "num_seeds": 5,
                "num_complete": 50,
                "num_leechs": 2,
                "num_incomplete": 10,
                "eta": 2000,
                "added_on": ${System.currentTimeMillis() / 1000},
                "completion_on": 0,
                "dl_limit": -1,
                "up_limit": -1,
                "save_path": "/downloads",
                "content_path": "/downloads/iso1.iso",
                "priority": 0,
                "time_active": 1000,
                "availability": 0.7
            }
        ]
        """.trimIndent()

        coEvery {
            requestManager.request<String>(mockServer.id, any())
        } returns RequestResult.Success(mockResponse)

        torrentRepository.refreshTorrents(mockServer)

        // When
        val movies = torrentRepository.getTorrentsByCategory("Movies")
        val isos = torrentRepository.getTorrentsByCategory("ISOs")

        // Then
        assertEquals(1, movies.size)
        assertEquals("Movie 1", movies[0].name)
        assertEquals(1, isos.size)
        assertEquals("ISO 1", isos[0].name)
    }

    @Test
    fun `getTorrentsByTag should return correct torrents`() = runTest {
        // Given
        val mockResponse = """
        [
            {
                "hash": "abc123",
                "name": "Tagged Torrent 1",
                "size": 1000000,
                "progress": 1.0,
                "dlspeed": 0,
                "upspeed": 1000,
                "state": "seeding",
                "category": "",
                "tags": "hd,action",
                "ratio": 1.0,
                "num_seeds": 0,
                "num_complete": 100,
                "num_leechs": 0,
                "num_incomplete": 0,
                "eta": 0,
                "added_on": ${System.currentTimeMillis() / 1000},
                "completion_on": ${(System.currentTimeMillis() / 1000) - 3600},
                "dl_limit": -1,
                "up_limit": -1,
                "save_path": "/downloads",
                "content_path": "/downloads/torrent1.mp4",
                "priority": 0,
                "time_active": 3600,
                "availability": 1.0
            },
            {
                "hash": "def456",
                "name": "Tagged Torrent 2",
                "size": 2000000,
                "progress": 0.5,
                "dlspeed": 1000,
                "upspeed": 500,
                "state": "downloading",
                "category": "",
                "tags": "hd,comedy",
                "ratio": 0.0,
                "num_seeds": 10,
                "num_complete": 50,
                "num_leechs": 5,
                "num_incomplete": 20,
                "eta": 1000,
                "added_on": ${System.currentTimeMillis() / 1000},
                "completion_on": 0,
                "dl_limit": -1,
                "up_limit": -1,
                "save_path": "/downloads",
                "content_path": "/downloads/torrent2.mp4",
                "priority": 0,
                "time_active": 1800,
                "availability": 0.8
            }
        ]
        """.trimIndent()

        coEvery {
            requestManager.request<String>(mockServer.id, any())
        } returns RequestResult.Success(mockResponse)

        torrentRepository.refreshTorrents(mockServer)

        // When
        val hdTorrents = torrentRepository.getTorrentsByTag("hd")
        val actionTorrents = torrentRepository.getTorrentsByTag("action")

        // Then
        assertEquals(2, hdTorrents.size)
        assertEquals(1, actionTorrents.size)
        assertEquals("Tagged Torrent 1", actionTorrents[0].name)
    }
}