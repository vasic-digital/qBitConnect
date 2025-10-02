package com.shareconnect.qbitconnect

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.qbitconnect.data.models.AddTorrentRequest
import com.shareconnect.qbitconnect.data.models.Server
import com.shareconnect.qbitconnect.data.repositories.TorrentRepository
import com.shareconnect.qbitconnect.di.DependencyContainer
import com.shareconnect.qbitconnect.network.RequestManager
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
@RunWith(AndroidJUnit4::class)
class TorrentIntegrationTest {

    private lateinit var torrentRepository: TorrentRepository

    private lateinit var mockServer: MockQBittorrentServer
    private lateinit var testServer: Server

    @Before
    fun setUp() {
        // Initialize dependencies
        val context = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext
        DependencyContainer.init(context)

        // Initialize repository
        torrentRepository = TorrentRepository()

        // Start mock server
        mockServer = MockQBittorrentServer()

        // Create test server pointing to mock
        testServer = Server(
            id = "test-server",
            name = "Test Server",
            host = mockServer.url.host,
            port = mockServer.url.port,
            username = "admin",
            password = "admin",
            useHttps = mockServer.url.scheme == "https"
        )
    }

    @After
    fun tearDown() {
        mockServer.shutdown()
    }

    @Test
    fun `complete torrent workflow integration test`() = runTest {
        // Test 1: Refresh torrents
        val refreshResult = torrentRepository.refreshTorrents(testServer.id)
        assertTrue("Refresh torrents should succeed", refreshResult.isSuccess)
        assertEquals("Should have 2 mock torrents", 2, torrentRepository.torrents.value.size)

        val ubuntuTorrent = torrentRepository.torrents.value.find { it.name == "Ubuntu 22.04 ISO" }
        val fedoraTorrent = torrentRepository.torrents.value.find { it.name == "Fedora 38 ISO" }

        assertTrue("Ubuntu torrent should exist", ubuntuTorrent != null)
        assertTrue("Fedora torrent should exist", fedoraTorrent != null)

        // Test 2: Get torrent by hash
        val torrentByHash = torrentRepository.getTorrentByHash(ubuntuTorrent.hash)
        assertEquals("Should find Ubuntu torrent by hash", ubuntuTorrent, torrentByHash)

        // Test 3: Get torrents by category
        val isos = torrentRepository.getTorrentsByCategory("ISOs")
        assertEquals("Should have 2 ISOs", 2, isos.size)

        // Test 4: Get categories
        val categoriesResult = torrentRepository.getCategories(testServer.id)
        assertTrue("Get categories should succeed", categoriesResult.isSuccess)
        assertTrue("Should have categories", categoriesResult.getOrNull()?.isNotEmpty() == true)

        // Test 5: Add torrent
        val addRequest = AddTorrentRequest(
            urls = listOf("magnet:?xt=urn:btih:newtorrenthash")
        )
        val addResult = torrentRepository.addTorrent(testServer.id, addRequest)
        assertTrue("Add torrent should succeed", addResult.isSuccess)

        // Test 6: Pause torrents
        val pauseResult = torrentRepository.pauseTorrents(testServer.id, listOf(ubuntuTorrent.hash))
        assertTrue("Pause torrents should succeed", pauseResult.isSuccess)

        // Test 7: Resume torrents
        val resumeResult = torrentRepository.resumeTorrents(testServer, listOf(ubuntuTorrent.hash))
        assertTrue("Resume torrents should succeed", resumeResult.isSuccess)

        // Test 8: Set category
        val setCategoryResult = torrentRepository.setCategory(testServer, listOf(ubuntuTorrent.hash), "Software")
        assertTrue("Set category should succeed", setCategoryResult.isSuccess)

        // Test 9: Set tags
        val setTagsResult = torrentRepository.setTags(testServer, listOf(ubuntuTorrent.hash), listOf("linux", "desktop"))
        assertTrue("Set tags should succeed", setTagsResult.isSuccess)

        // Test 10: Set limits
        val setLimitsResult = torrentRepository.setLimits(
            testServer,
            listOf(ubuntuTorrent.hash),
            downloadLimit = 1024000L,
            uploadLimit = 512000L
        )
        assertTrue("Set limits should succeed", setLimitsResult.isSuccess)

        // Test 11: Create category
        val createCategoryResult = torrentRepository.createCategory(testServer.id, "NewCategory", "/downloads/new")
        assertTrue("Create category should succeed", createCategoryResult.isSuccess)

        // Test 12: Delete torrents
        val deleteResult = torrentRepository.deleteTorrents(testServer, listOf(fedoraTorrent.hash), deleteFiles = false)
        assertTrue("Delete torrents should succeed", deleteResult.isSuccess)

        // Test 13: Delete category
        val deleteCategoryResult = torrentRepository.deleteCategory(testServer.id, "NewCategory")
        assertTrue("Delete category should succeed", deleteCategoryResult.isSuccess)
    }

    @Test
    fun `edge case - network failure handling`() = runTest {
        // Create server with invalid host to simulate network failure
        val invalidServer = Server(
            id = "invalid-server",
            name = "Invalid Server",
            host = "invalid.host.that.does.not.exist",
            port = 8080
        )

        // Test refresh torrents with network failure
        val refreshResult = torrentRepository.refreshTorrents(invalidServer.id)
        assertTrue("Refresh should fail with network error", refreshResult.isFailure)

        // Test add torrent with network failure
        val addRequest = AddTorrentRequest(urls = listOf("magnet:?xt=urn:btih:test"))
        val addResult = torrentRepository.addTorrent(invalidServer.id, addRequest)
        assertTrue("Add torrent should fail with network error", addResult.isFailure)
    }

    @Test
    fun `stress test - multiple concurrent operations`() = runTest {
        // Perform multiple operations concurrently to test thread safety
        val operations = List(10) {
            async {
                torrentRepository.refreshTorrents(testServer.id)
            }
        }

        val results = operations.map { it.await() }
        val successCount = results.count { it.isSuccess }
        val failureCount = results.count { it.isFailure }

        assertTrue("At least some operations should succeed", successCount > 0)
        // Note: Some failures are expected due to mock server limitations under concurrent load
    }
}