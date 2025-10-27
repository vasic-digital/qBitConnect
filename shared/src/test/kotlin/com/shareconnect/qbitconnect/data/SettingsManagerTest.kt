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


package com.shareconnect.qbitconnect.data

import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.Settings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsManagerTest {

    private val settings: Settings = MapSettings()
    private lateinit var settingsManager: SettingsManager

    private fun setupSettingsManager() {
        settingsManager = SettingsManager(settings)
    }

    @Test
    fun `theme should have correct default value`() {
        setupSettingsManager()

        assertEquals(Theme.SYSTEM_DEFAULT, settingsManager.theme.value)
    }

    @Test
    fun `enableDynamicColors should have correct default value`() {
        setupSettingsManager()

        assertTrue(settingsManager.enableDynamicColors.value)
    }

    @Test
    fun `pureBlackDarkMode should have correct default value`() {
        setupSettingsManager()

        assertFalse(settingsManager.pureBlackDarkMode.value)
    }

    @Test
    fun `showRelativeTimestamps should have correct default value`() {
        setupSettingsManager()

        assertTrue(settingsManager.showRelativeTimestamps.value)
    }

    @Test
    fun `sort should have correct default value`() {
        setupSettingsManager()

        assertEquals(TorrentSort.NAME, settingsManager.sort.value)
    }

    @Test
    fun `isReverseSorting should have correct default value`() {
        setupSettingsManager()

        assertFalse(settingsManager.isReverseSorting.value)
    }

    @Test
    fun `connectionTimeout should have correct default value`() {
        setupSettingsManager()

        assertEquals(10, settingsManager.connectionTimeout.value)
    }

    @Test
    fun `autoRefreshInterval should have correct default value`() {
        setupSettingsManager()

        assertEquals(3, settingsManager.autoRefreshInterval.value)
    }

    @Test
    fun `notificationCheckInterval should have correct default value`() {
        setupSettingsManager()

        assertEquals(15, settingsManager.notificationCheckInterval.value)
    }

    @Test
    fun `areTorrentSwipeActionsEnabled should have correct default value`() {
        setupSettingsManager()

        assertTrue(settingsManager.areTorrentSwipeActionsEnabled.value)
    }

    @Test
    fun `defaultTorrentStatus should have correct default value`() {
        setupSettingsManager()

        assertEquals(TorrentFilter.ALL, settingsManager.defaultTorrentStatus.value)
    }

    @Test
    fun `areStatesCollapsed should have correct default value`() {
        setupSettingsManager()

        assertFalse(settingsManager.areStatesCollapsed.value)
    }

    @Test
    fun `areCategoriesCollapsed should have correct default value`() {
        setupSettingsManager()

        assertFalse(settingsManager.areCategoriesCollapsed.value)
    }

    @Test
    fun `areTagsCollapsed should have correct default value`() {
        setupSettingsManager()

        assertFalse(settingsManager.areTagsCollapsed.value)
    }

    @Test
    fun `areTrackersCollapsed should have correct default value`() {
        setupSettingsManager()

        assertFalse(settingsManager.areTrackersCollapsed.value)
    }

    @Test
    fun `searchSort should have correct default value`() {
        setupSettingsManager()

        assertEquals(SearchSort.NAME, settingsManager.searchSort.value)
    }

    @Test
    fun `isReverseSearchSorting should have correct default value`() {
        setupSettingsManager()

        assertFalse(settingsManager.isReverseSearchSorting.value)
    }

    @Test
    fun `checkUpdates should have correct default value`() {
        setupSettingsManager()

        assertTrue(settingsManager.checkUpdates.value)
    }

    @Test
    fun `settings should persist values correctly`() {
        setupSettingsManager()

        // Change some values
        settingsManager.theme.value = Theme.DARK
        settingsManager.connectionTimeout.value = 20
        settingsManager.enableDynamicColors.value = false

        // Create new instance to test persistence
        val newSettingsManager = SettingsManager(settings)

        assertEquals(Theme.DARK, newSettingsManager.theme.value)
        assertEquals(20, newSettingsManager.connectionTimeout.value)
        assertFalse(newSettingsManager.enableDynamicColors.value)
    }

    @Test
    fun `Theme enum should have correct values`() {
        assertEquals("LIGHT", Theme.LIGHT.name)
        assertEquals("DARK", Theme.DARK.name)
        assertEquals("SYSTEM_DEFAULT", Theme.SYSTEM_DEFAULT.name)
    }

    @Test
    fun `TorrentSort enum should have correct values`() {
        assertEquals("NAME", TorrentSort.NAME.name)
        assertEquals("STATUS", TorrentSort.STATUS.name)
        assertEquals("HASH", TorrentSort.HASH.name)
        // Test a few more
        assertEquals("DOWNLOAD_SPEED", TorrentSort.DOWNLOAD_SPEED.name)
        assertEquals("UPLOAD_SPEED", TorrentSort.UPLOAD_SPEED.name)
    }

    @Test
    fun `SearchSort enum should have correct values`() {
        assertEquals("NAME", SearchSort.NAME.name)
        assertEquals("SIZE", SearchSort.SIZE.name)
        assertEquals("SEEDERS", SearchSort.SEEDERS.name)
        assertEquals("LEECHERS", SearchSort.LEECHERS.name)
        assertEquals("SEARCH_ENGINE", SearchSort.SEARCH_ENGINE.name)
    }

    @Test
    fun `TorrentFilter enum should have correct values`() {
        assertEquals("ALL", TorrentFilter.ALL.name)
        assertEquals("DOWNLOADING", TorrentFilter.DOWNLOADING.name)
        assertEquals("SEEDING", TorrentFilter.SEEDING.name)
        assertEquals("COMPLETED", TorrentFilter.COMPLETED.name)
        assertEquals("PAUSED", TorrentFilter.PAUSED.name)
        assertEquals("ACTIVE", TorrentFilter.ACTIVE.name)
        assertEquals("INACTIVE", TorrentFilter.INACTIVE.name)
        assertEquals("STALLED", TorrentFilter.STALLED.name)
        assertEquals("CHECKING", TorrentFilter.CHECKING.name)
        assertEquals("ERRORED", TorrentFilter.ERRORED.name)
    }
}