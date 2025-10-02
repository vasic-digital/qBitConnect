package com.shareconnect.qbitconnect.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsManagerTest {

    private lateinit var testDataStore: DataStore<Preferences>
    private lateinit var settingsManager: SettingsManager
    private lateinit var testScope: TestScope

    @Before
    fun setup() {
        testScope = TestScope()
        testDataStore = PreferenceDataStoreFactory.create(
            scope = testScope.backgroundScope,
            produceFile = { File.createTempFile("test", ".preferences_pb") }
        )
        settingsManager = SettingsManager(testDataStore = testDataStore)
    }

    @After
    fun cleanup() {
        // No cleanup needed for in-memory DataStore
    }

    @Test
    fun `default theme is SYSTEM_DEFAULT`() = testScope.runTest {
        val theme = settingsManager.theme.first()
        assertEquals(Theme.SYSTEM_DEFAULT, theme)
    }

    @Test
    fun `default dynamic colors is true`() = testScope.runTest {
        val enableDynamicColors = settingsManager.enableDynamicColors.first()
        assertEquals(true, enableDynamicColors)
    }

    @Test
    fun `setTheme updates theme correctly`() = testScope.runTest {
        settingsManager.setTheme(Theme.DARK)

        val theme = settingsManager.theme.first()
        assertEquals(Theme.DARK, theme)
    }

    @Test
    fun `setEnableDynamicColors updates setting correctly`() = testScope.runTest {
        settingsManager.setEnableDynamicColors(false)

        val enableDynamicColors = settingsManager.enableDynamicColors.first()
        assertEquals(false, enableDynamicColors)
    }

    private suspend fun <T> kotlinx.coroutines.flow.Flow<T>.first(): T {
        var result: T? = null
        this.collect { value ->
            result = value
            return@collect
        }
        return result!!
    }
}