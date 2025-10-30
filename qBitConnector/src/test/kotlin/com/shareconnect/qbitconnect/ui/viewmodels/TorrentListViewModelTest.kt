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


package com.shareconnect.qbitconnect.ui.viewmodels

import com.shareconnect.qbitconnect.data.models.Server
import com.shareconnect.qbitconnect.data.models.Torrent
import com.shareconnect.qbitconnect.data.models.TorrentState
import com.shareconnect.qbitconnect.data.repositories.ServerRepository
import com.shareconnect.qbitconnect.data.repositories.TorrentRepository
import com.shareconnect.qbitconnect.model.RequestResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Instant
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], application = com.shareconnect.qbitconnect.TestApplication::class)
class TorrentListViewModelTest {

    private lateinit var torrentRepository: TorrentRepository
    private lateinit var serverRepository: ServerRepository
    private lateinit var viewModel: TorrentListViewModel
    private lateinit var testDispatcher: TestDispatcher

    private val mockServer = Server(
        id = 1,
        name = "Test Server",
        host = "localhost",
        port = 8080,
        username = "admin",
        password = "admin"
    )

    private val sampleTorrents = listOf(
        Torrent(
            hash = "abc123",
            name = "Ubuntu ISO",
            size = 1000000L,
            progress = 0.5f,
            downloadSpeed = 1000L,
            uploadSpeed = 500L,
            downloaded = 500000L,
            uploaded = 250000L,
            eta = 3600L,
            state = TorrentState.DOWNLOADING,
            category = "ISOs",
            tags = listOf("linux", "ubuntu"),
            ratio = 0.5f,
            addedOn = Instant.fromEpochSeconds(1640995200),
            completionOn = null,
            savePath = "/downloads",
            contentPath = "/downloads/ubuntu.iso",
            priority = 0,
            seeds = 10,
            seedsTotal = 50,
            peers = 5,
            peersTotal = 20,
            downloadLimit = -1L,
            uploadLimit = -1L,
            timeActive = 1800L,
            availability = 0.8f
        ),
        Torrent(
            hash = "def456",
            name = "Movie File",
            size = 2000000L,
            progress = 1.0f,
            downloadSpeed = 0L,
            uploadSpeed = 1000L,
            downloaded = 2000000L,
            uploaded = 1000000L,
            eta = 0L,
            state = TorrentState.SEEDING,
            category = "Movies",
            tags = listOf("hd", "action"),
            ratio = 0.5f,
            addedOn = Instant.fromEpochSeconds(1640995200),
            completionOn = Instant.fromEpochSeconds(1640999200),
            savePath = "/downloads",
            contentPath = "/downloads/movie.mp4",
            priority = 0,
            seeds = 0,
            seedsTotal = 100,
            peers = 0,
            peersTotal = 0,
            downloadLimit = -1L,
            uploadLimit = -1L,
            timeActive = 3600L,
            availability = 1.0f
        )
    )

    @Before
    fun setup() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        torrentRepository = mockk()
        serverRepository = mockk()

        every { torrentRepository.torrents } returns flowOf(sampleTorrents)
        every { torrentRepository.categories } returns flowOf(listOf("ISOs", "Movies"))
        every { torrentRepository.tags } returns flowOf(listOf("linux", "ubuntu", "hd", "action"))
        every { serverRepository.activeServer } returns flowOf(mockServer)

        coEvery { torrentRepository.refreshTorrents(any()) } returns RequestResult.Success(Unit)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): TorrentListViewModel {
        return TorrentListViewModel(torrentRepository, serverRepository)
    }

    @Test
    fun `initial state should have correct default values`() = runTest {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.error.value)
        assertNull(viewModel.selectedCategory.value)
        assertNull(viewModel.selectedTag.value)
        assertEquals("", viewModel.searchQuery.value)
    }

    @Test
    fun `should call refreshTorrents on init`() = runTest {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { torrentRepository.refreshTorrents(mockServer.id) }
    }

    @Test
    fun `refreshTorrents should handle success`() = runTest {
        coEvery { torrentRepository.refreshTorrents(any()) } returns RequestResult.Success(Unit)

        viewModel = createViewModel()
        viewModel.refreshTorrents()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun `refreshTorrents should handle error`() = runTest {
        coEvery { torrentRepository.refreshTorrents(any()) } returns RequestResult.Error.ApiError(500)

        viewModel = createViewModel()
        viewModel.refreshTorrents()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
        assertEquals("Failed to refresh torrents", viewModel.error.value)
    }

    @Test
    fun `refreshTorrents should handle no active server`() = runTest {
        every { serverRepository.activeServer } returns flowOf(null)

        viewModel = createViewModel()
        viewModel.refreshTorrents()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("No active server selected", viewModel.error.value)
    }

    @Test
    fun `refreshTorrents should handle exception`() = runTest {
        coEvery { torrentRepository.refreshTorrents(any()) } throws RuntimeException("Network error")

        viewModel = createViewModel()
        viewModel.refreshTorrents()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Failed to refresh torrents: Network error", viewModel.error.value)
    }

    @Test
    fun `filteredTorrents should filter by category`() = runTest {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val results = mutableListOf<List<Torrent>>()
        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.filteredTorrents.toList(results)
        }

        viewModel.setSelectedCategory("ISOs")
        testDispatcher.scheduler.advanceUntilIdle()

        job.cancel()

        // Should have initial unfiltered results and filtered results
        assertTrue(results.size >= 2)
        val filteredResults = results.last()
        assertEquals(1, filteredResults.size)
        assertEquals("Ubuntu ISO", filteredResults[0].name)
    }

    @Test
    fun `filteredTorrents should filter by tag`() = runTest {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val results = mutableListOf<List<Torrent>>()
        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.filteredTorrents.toList(results)
        }

        viewModel.setSelectedTag("action")
        testDispatcher.scheduler.advanceUntilIdle()

        job.cancel()

        assertTrue(results.size >= 2)
        val filteredResults = results.last()
        assertEquals(1, filteredResults.size)
        assertEquals("Movie File", filteredResults[0].name)
    }

    @Test
    fun `filteredTorrents should filter by search query`() = runTest {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val results = mutableListOf<List<Torrent>>()
        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.filteredTorrents.toList(results)
        }

        viewModel.setSearchQuery("ubuntu")
        testDispatcher.scheduler.advanceUntilIdle()

        job.cancel()

        assertTrue(results.size >= 2)
        val filteredResults = results.last()
        assertEquals(1, filteredResults.size)
        assertEquals("Ubuntu ISO", filteredResults[0].name)
    }

    @Test
    fun `filteredTorrents should filter by hash in search query`() = runTest {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val results = mutableListOf<List<Torrent>>()
        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.filteredTorrents.toList(results)
        }

        viewModel.setSearchQuery("abc123")
        testDispatcher.scheduler.advanceUntilIdle()

        job.cancel()

        assertTrue(results.size >= 2)
        val filteredResults = results.last()
        assertEquals(1, filteredResults.size)
        assertEquals("Ubuntu ISO", filteredResults[0].name)
    }

    @Test
    fun `clearFilters should reset all filters`() {
        viewModel = createViewModel()

        viewModel.setSelectedCategory("ISOs")
        viewModel.setSelectedTag("linux")
        viewModel.setSearchQuery("ubuntu")

        viewModel.clearFilters()

        assertNull(viewModel.selectedCategory.value)
        assertNull(viewModel.selectedTag.value)
        assertEquals("", viewModel.searchQuery.value)
    }

    @Test
    fun `pauseTorrents should call repository and refresh on success`() = runTest {
        coEvery { torrentRepository.pauseTorrents(any(), any()) } returns RequestResult.Success(Unit)

        viewModel = createViewModel()
        viewModel.pauseTorrents(listOf("abc123"))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { torrentRepository.pauseTorrents(mockServer.id, listOf("abc123")) }
        coVerify(exactly = 2) { torrentRepository.refreshTorrents(mockServer.id) } // Init + after action
    }

    @Test
    fun `pauseTorrents should handle error`() = runTest {
        coEvery { torrentRepository.pauseTorrents(any(), any()) } returns RequestResult.Error.ApiError(500)

        viewModel = createViewModel()
        viewModel.pauseTorrents(listOf("abc123"))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Action failed", viewModel.error.value)
    }

    @Test
    fun `resumeTorrents should call repository`() = runTest {
        coEvery { torrentRepository.resumeTorrents(any(), any()) } returns RequestResult.Success(Unit)

        viewModel = createViewModel()
        viewModel.resumeTorrents(listOf("def456"))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { torrentRepository.resumeTorrents(mockServer.id, listOf("def456")) }
    }

    @Test
    fun `deleteTorrents should call repository with correct parameters`() = runTest {
        coEvery { torrentRepository.deleteTorrents(any(), any(), any()) } returns RequestResult.Success(Unit)

        viewModel = createViewModel()
        viewModel.deleteTorrents(listOf("abc123"), deleteFiles = true)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { torrentRepository.deleteTorrents(mockServer.id, listOf("abc123"), true) }
    }

    @Test
    fun `setCategory should call repository`() = runTest {
        coEvery { torrentRepository.setCategory(any(), any(), any()) } returns RequestResult.Success(Unit)

        viewModel = createViewModel()
        viewModel.setCategory(listOf("abc123"), "New Category")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { torrentRepository.setCategory(mockServer.id, listOf("abc123"), "New Category") }
    }

    @Test
    fun `setTags should call repository`() = runTest {
        coEvery { torrentRepository.setTags(any(), any(), any()) } returns RequestResult.Success(Unit)

        viewModel = createViewModel()
        viewModel.setTags(listOf("abc123"), listOf("tag1", "tag2"))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { torrentRepository.setTags(mockServer.id, listOf("abc123"), listOf("tag1", "tag2")) }
    }

    @Test
    fun `setLimits should call repository`() = runTest {
        coEvery { torrentRepository.setLimits(any(), any(), any(), any()) } returns RequestResult.Success(Unit)

        viewModel = createViewModel()
        viewModel.setLimits(listOf("abc123"), downloadLimit = 1000L, uploadLimit = 500L)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { torrentRepository.setLimits(mockServer.id, listOf("abc123"), 1000L, 500L) }
    }

    @Test
    fun `performTorrentAction should handle no active server`() = runTest {
        every { serverRepository.activeServer } returns flowOf(null)

        viewModel = createViewModel()
        viewModel.pauseTorrents(listOf("abc123"))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("No active server selected", viewModel.error.value)
    }

    @Test
    fun `performTorrentAction should handle exception`() = runTest {
        coEvery { torrentRepository.pauseTorrents(any(), any()) } throws RuntimeException("Network error")

        viewModel = createViewModel()
        viewModel.pauseTorrents(listOf("abc123"))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Action failed: Network error", viewModel.error.value)
    }

    @Test
    fun `clearError should reset error state`() {
        viewModel = createViewModel()
        viewModel.clearError()
        assertNull(viewModel.error.value)
    }
}