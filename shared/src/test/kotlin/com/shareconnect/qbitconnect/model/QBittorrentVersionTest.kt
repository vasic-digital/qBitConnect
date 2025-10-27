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