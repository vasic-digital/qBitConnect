package com.shareconnect.qbitconnect.data.repositories

import com.shareconnect.qbitconnect.data.models.AddTorrentRequest
import com.shareconnect.qbitconnect.data.models.Server
import com.shareconnect.qbitconnect.network.RequestManager
import com.shareconnect.qbitconnect.model.RequestResult
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
            id = 1,
            name = "Test Server",
            host = "localhost",
            port = 8080,
            username = "admin",
            password = "admin"
        )
    }

    // @Test
    // fun `refreshTorrents should return success when API call succeeds`() = runTest {
    //     // Given
    //     val mockResponse = """
    //     [
    //         {
    //             "hash": "abc123def456",
    //             "name": "Ubuntu 22.04 ISO",
    //             "size": 3221225472,
    //             "progress": 0.75,
    //             "dlspeed": 1024000,
    //             "upspeed": 512000,
    //             "state": "downloading",
    //             "category": "ISOs",
    //             "tags": "linux,ubuntu",
    //             "ratio": 0.5,
    //             "num_seeds": 25,
    //             "num_complete": 150,
    //             "num_leechs": 10,
    //             "num_incomplete": 50,
    //             "eta": 3600,
    //             "added_on": ${System.currentTimeMillis() / 1000},
    //             "completion_on": 0,
    //             "dl_limit": -1,
    //             "up_limit": -1,
    //             "save_path": "/downloads",
    //             "content_path": "/downloads/ubuntu.iso",
    //             "priority": 0,
    //             "time_active": 1800,
    //             "availability": 0.8
    //         },
    //         {
    //             "hash": "def789ghi012",
    //             "name": "Movie 1",
    //             "size": 2147483648,
    //             "progress": 1.0,
    //             "dlspeed": 0,
    //             "upspeed": 256000,
    //             "state": "seeding",
    //             "category": "Movies",
    //             "tags": "hd,action",
    //             "ratio": 1.2,
    //             "num_seeds": 0,
    //             "num_complete": 200,
    //             "num_leechs": 0,
    //             "num_incomplete": 0,
    //             "eta": 0,
    //             "added_on": ${System.currentTimeMillis() / 1000 - 86400},
    //             "completion_on": ${System.currentTimeMillis() / 1000 - 3600},
    //             "dl_limit": -1,
    //             "up_limit": -1,
    //             "save_path": "/downloads",
    //             "content_path": "/downloads/movie1.mp4",
    //             "priority": 0,
    //             "time_active": 7200,
    //             "availability": 1.0
    //         }
    //     ]
    //     """.trimIndent()

    //     coEvery {
    //         requestManager.request<String>(any(), any())
    //     } returns RequestResult.Success(mockResponse)

    //     val result = torrentRepository.refreshTorrents(mockServer.id)
    //     assertTrue(result is RequestResult.Success)

    //     // When
    //     val movies = torrentRepository.getTorrentsByCategory("Movies")
    //     val isos = torrentRepository.getTorrentsByCategory("ISOs")

    //     // Then
    //     assertEquals(1, movies.size)
    //     assertEquals("Movie 1", movies[0].name)
    //     assertEquals(1, isos.size)
    //     assertEquals("Ubuntu 22.04 ISO", isos[0].name)
    // }

    // @Test
    // fun `getTorrentsByTag should return correct torrents`() = runTest {
    //     // Given
    //     val mockResponse = """
    //     [
    //         {
    //             "hash": "abc123",
    //             "name": "Tagged Torrent 1",
    //             "size": 1000000,
    //             "progress": 1.0,
    //             "dlspeed": 0,
    //             "upspeed": 1000,
    //             "state": "seeding",
    //             "category": "",
    //             "tags": "hd,action",
    //             "ratio": 1.0,
    //             "num_seeds": 0,
    //             "num_complete": 100,
    //             "num_leechs": 0,
    //             "num_incomplete": 0,
    //             "eta": 0,
    //             "added_on": ${System.currentTimeMillis() / 1000},
    //             "completion_on": ${(System.currentTimeMillis() / 1000) - 3600},
    //             "dl_limit": -1,
    //             "up_limit": -1,
    //             "save_path": "/downloads",
    //             "content_path": "/downloads/torrent1.mp4",
    //             "priority": 0,
    //             "time_active": 3600,
    //             "availability": 1.0
    //         },
    //         {
    //             "hash": "def456",
    //             "name": "Tagged Torrent 2",
    //             "size": 2000000,
    //             "progress": 0.5,
    //             "dlspeed": 1000,
    //             "upspeed": 500,
    //             "state": "downloading",
    //             "category": "",
    //             "tags": "hd,comedy",
    //             "ratio": 0.0,
    //             "num_seeds": 10,
    //             "num_complete": 50,
    //             "num_leechs": 5,
    //             "num_incomplete": 20,
    //             "eta": 1000,
    //             "added_on": ${System.currentTimeMillis() / 1000},
    //             "completion_on": 0,
    //             "dl_limit": -1,
    //             "up_limit": -1,
    //             "save_path": "/downloads",
    //             "content_path": "/downloads/torrent2.mp4",
    //             "priority": 0,
    //             "time_active": 1800,
    //             "availability": 0.8
    //         }
    //     ]
    //     """.trimIndent()

    //     coEvery {
    //         requestManager.request<String>(any(), any())
    //     } returns RequestResult.Success(mockResponse)

    //     val result = torrentRepository.refreshTorrents(mockServer.id)
    //     assertTrue(result is RequestResult.Success)

    //     // When
    //     val hdTorrents = torrentRepository.getTorrentsByTag("hd")
    //     val actionTorrents = torrentRepository.getTorrentsByTag("action")

    //     // Then
    //     assertEquals(2, hdTorrents.size)
    //     assertEquals(1, actionTorrents.size)
    //     assertEquals("Tagged Torrent 1", actionTorrents[0].name)
    // }
}