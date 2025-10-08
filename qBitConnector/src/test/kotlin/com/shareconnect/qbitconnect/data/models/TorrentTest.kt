package com.shareconnect.qbitconnect.data.models

import kotlinx.datetime.Instant
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TorrentTest {

    @Test
    fun `Torrent should have correct default values`() {
        val torrent = Torrent(
            hash = "abc123def456",
            name = "Test Torrent",
            size = 1024L,
            progress = 0.5f,
            downloadSpeed = 1000L,
            uploadSpeed = 500L,
            downloaded = 512L,
            uploaded = 256L,
            eta = 3600L,
            state = TorrentState.DOWNLOADING,
            ratio = 0.5f,
            addedOn = Instant.fromEpochSeconds(1640995200),
            savePath = "/downloads",
            contentPath = "/downloads/test.file",
            timeActive = 1800L,
            availability = 0.8f
        )

        assertEquals("abc123def456", torrent.hash)
        assertEquals("Test Torrent", torrent.name)
        assertEquals(1024L, torrent.size)
        assertEquals(0.5f, torrent.progress)
        assertEquals(1000L, torrent.downloadSpeed)
        assertEquals(500L, torrent.uploadSpeed)
        assertEquals(512L, torrent.downloaded)
        assertEquals(256L, torrent.uploaded)
        assertEquals(3600L, torrent.eta)
        assertEquals(TorrentState.DOWNLOADING, torrent.state)
        assertNull(torrent.category)
        assertTrue(torrent.tags.isEmpty())
        assertEquals(0.5f, torrent.ratio)
        assertEquals(Instant.fromEpochSeconds(1640995200), torrent.addedOn)
        assertNull(torrent.completionOn)
        assertEquals("/downloads", torrent.savePath)
        assertEquals("/downloads/test.file", torrent.contentPath)
        assertEquals(0, torrent.priority)
        assertEquals(0, torrent.seeds)
        assertEquals(0, torrent.seedsTotal)
        assertEquals(0, torrent.peers)
        assertEquals(0, torrent.peersTotal)
        assertEquals(-1L, torrent.downloadLimit)
        assertEquals(-1L, torrent.uploadLimit)
        assertEquals(1800L, torrent.timeActive)
        assertEquals(0.8f, torrent.availability)
    }

    @Test
    fun `Torrent should support optional category and tags`() {
        val torrent = Torrent(
            hash = "test123",
            name = "Categorized Torrent",
            size = 2048L,
            progress = 1.0f,
            downloadSpeed = 0L,
            uploadSpeed = 1000L,
            downloaded = 2048L,
            uploaded = 1024L,
            eta = 0L,
            state = TorrentState.SEEDING,
            category = "Movies",
            tags = listOf("hd", "action", "2023"),
            ratio = 0.5f,
            addedOn = Instant.fromEpochSeconds(1640995200),
            completionOn = Instant.fromEpochSeconds(1640999200),
            savePath = "/downloads",
            contentPath = "/downloads/movie.mp4",
            timeActive = 3600L,
            availability = 1.0f
        )

        assertEquals("Movies", torrent.category)
        assertEquals(listOf("hd", "action", "2023"), torrent.tags)
        assertEquals(Instant.fromEpochSeconds(1640999200), torrent.completionOn)
    }

    @Test
    fun `Torrent should support custom priority and limits`() {
        val torrent = Torrent(
            hash = "priority123",
            name = "Priority Torrent",
            size = 1024L,
            progress = 0.3f,
            downloadSpeed = 2000L,
            uploadSpeed = 1000L,
            downloaded = 307L,
            uploaded = 150L,
            eta = 2400L,
            state = TorrentState.DOWNLOADING,
            ratio = 0.49f,
            addedOn = Instant.fromEpochSeconds(1640995200),
            savePath = "/downloads",
            contentPath = "/downloads/file.bin",
            priority = 5,
            seeds = 15,
            seedsTotal = 100,
            peers = 10,
            peersTotal = 50,
            downloadLimit = 1024000L, // 1 MB/s
            uploadLimit = 512000L,    // 512 KB/s
            timeActive = 1200L,
            availability = 0.9f
        )

        assertEquals(5, torrent.priority)
        assertEquals(15, torrent.seeds)
        assertEquals(100, torrent.seedsTotal)
        assertEquals(10, torrent.peers)
        assertEquals(50, torrent.peersTotal)
        assertEquals(1024000L, torrent.downloadLimit)
        assertEquals(512000L, torrent.uploadLimit)
    }

    @Test
    fun `TorrentState should have all expected values`() {
        val expectedStates = setOf(
            TorrentState.ERROR,
            TorrentState.MISSING_FILES,
            TorrentState.UPLOADING,
            TorrentState.PAUSED_UP,
            TorrentState.QUEUED_UP,
            TorrentState.STALLED_UP,
            TorrentState.CHECKING_UP,
            TorrentState.FORCED_UP,
            TorrentState.ALLOCATING,
            TorrentState.DOWNLOADING,
            TorrentState.META_DL,
            TorrentState.PAUSED_DL,
            TorrentState.QUEUED_DL,
            TorrentState.STALLED_DL,
            TorrentState.CHECKING_DL,
            TorrentState.FORCED_DL,
            TorrentState.CHECKING_RESUME_DATA,
            TorrentState.MOVING,
            TorrentState.UNKNOWN
        )

        val actualStates = TorrentState.entries.toSet()
        assertEquals(expectedStates, actualStates)
    }

    @Test
    fun `TorrentState should have correct name mappings`() {
        assertEquals("ERROR", TorrentState.ERROR.name)
        assertEquals("MISSING_FILES", TorrentState.MISSING_FILES.name)
        assertEquals("UPLOADING", TorrentState.UPLOADING.name)
        assertEquals("DOWNLOADING", TorrentState.DOWNLOADING.name)
        assertEquals("SEEDING", TorrentState.UPLOADING.name)
        assertEquals("UNKNOWN", TorrentState.UNKNOWN.name)
    }

    @Test
    fun `Torrent data class should support equality and copy`() {
        val torrent1 = Torrent(
            hash = "same123",
            name = "Same Torrent",
            size = 1024L,
            progress = 0.5f,
            downloadSpeed = 1000L,
            uploadSpeed = 500L,
            downloaded = 512L,
            uploaded = 256L,
            eta = 3600L,
            state = TorrentState.DOWNLOADING,
            ratio = 0.5f,
            addedOn = Instant.fromEpochSeconds(1640995200),
            savePath = "/downloads",
            contentPath = "/downloads/same.file",
            timeActive = 1800L,
            availability = 0.8f
        )

        val torrent2 = torrent1.copy()
        assertEquals(torrent1, torrent2)

        val torrent3 = torrent1.copy(name = "Different Name")
        assertFalse(torrent1 == torrent3)
        assertEquals("Different Name", torrent3.name)
        assertEquals(torrent1.hash, torrent3.hash) // Other properties should remain the same
    }

    @Test
    fun `Torrent should handle edge case values`() {
        val torrent = Torrent(
            hash = "",
            name = "",
            size = 0L,
            progress = 0.0f,
            downloadSpeed = 0L,
            uploadSpeed = 0L,
            downloaded = 0L,
            uploaded = 0L,
            eta = 0L,
            state = TorrentState.UNKNOWN,
            ratio = 0.0f,
            addedOn = Instant.fromEpochSeconds(0),
            savePath = "",
            contentPath = "",
            timeActive = 0L,
            availability = 0.0f
        )

        assertEquals("", torrent.hash)
        assertEquals("", torrent.name)
        assertEquals(0L, torrent.size)
        assertEquals(0.0f, torrent.progress)
        assertEquals(0L, torrent.eta)
        assertEquals(TorrentState.UNKNOWN, torrent.state)
        assertEquals(0.0f, torrent.ratio)
        assertEquals(0L, torrent.timeActive)
        assertEquals(0.0f, torrent.availability)
    }

    @Test
    fun `Torrent should handle maximum values`() {
        val torrent = Torrent(
            hash = "max123",
            name = "Max Values Torrent",
            size = Long.MAX_VALUE,
            progress = 1.0f,
            downloadSpeed = Long.MAX_VALUE,
            uploadSpeed = Long.MAX_VALUE,
            downloaded = Long.MAX_VALUE,
            uploaded = Long.MAX_VALUE,
            eta = Long.MAX_VALUE,
            state = TorrentState.SEEDING,
            ratio = Float.MAX_VALUE,
            addedOn = Instant.fromEpochSeconds(Long.MAX_VALUE / 1000),
            savePath = "/downloads",
            contentPath = "/downloads/max.file",
            priority = Int.MAX_VALUE,
            seeds = Int.MAX_VALUE,
            seedsTotal = Int.MAX_VALUE,
            peers = Int.MAX_VALUE,
            peersTotal = Int.MAX_VALUE,
            downloadLimit = Long.MAX_VALUE,
            uploadLimit = Long.MAX_VALUE,
            timeActive = Long.MAX_VALUE,
            availability = 1.0f
        )

        assertEquals(Long.MAX_VALUE, torrent.size)
        assertEquals(1.0f, torrent.progress)
        assertEquals(Long.MAX_VALUE, torrent.downloadSpeed)
        assertEquals(Float.MAX_VALUE, torrent.ratio)
        assertEquals(Int.MAX_VALUE, torrent.priority)
        assertEquals(1.0f, torrent.availability)
    }
}