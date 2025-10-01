package com.shareconnect.qbitconnect.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QBittorrentVersionTest {

    @Test
    fun `Invalid should be invalid version`() {
        val version = QBittorrentVersion.Invalid

        assertTrue(version is QBittorrentVersion.Invalid)
    }

    @Test
    fun `Valid should contain version components`() {
        val major = 4
        val minor = 5
        val patch = 2
        val version = QBittorrentVersion.Valid(major, minor, patch)

        assertTrue(version is QBittorrentVersion.Valid)
        assertEquals(major, (version as QBittorrentVersion.Valid).major)
        assertEquals(minor, version.minor)
        assertEquals(patch, version.patch)
    }

    @Test
    fun `fromString should parse valid version string`() {
        val versionString = "4.5.2"
        val version = QBittorrentVersion.fromString(versionString)

        assertTrue(version is QBittorrentVersion.Valid)
        val validVersion = version as QBittorrentVersion.Valid
        assertEquals(4, validVersion.major)
        assertEquals(5, validVersion.minor)
        assertEquals(2, validVersion.patch)
    }

    @Test
    fun `fromString should parse version without patch`() {
        val versionString = "4.5"
        val version = QBittorrentVersion.fromString(versionString)

        assertTrue(version is QBittorrentVersion.Valid)
        val validVersion = version as QBittorrentVersion.Valid
        assertEquals(4, validVersion.major)
        assertEquals(5, validVersion.minor)
        assertEquals(0, validVersion.patch)
    }

    @Test
    fun `fromString should return Invalid for malformed version string`() {
        val invalidVersions = listOf(
            "",
            "4",
            "4.",
            ".5",
            "4.5.2.3",
            "a.b.c",
            "4.b.2"
        )

        invalidVersions.forEach { versionString ->
            val version = QBittorrentVersion.fromString(versionString)
            assertTrue("Version string '$versionString' should be invalid", version is QBittorrentVersion.Invalid)
        }
    }

    @Test
    fun `fromString should return Invalid for non-numeric components`() {
        val versionString = "4.abc.2"
        val version = QBittorrentVersion.fromString(versionString)

        assertTrue(version is QBittorrentVersion.Invalid)
    }
}