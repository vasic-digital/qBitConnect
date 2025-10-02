package com.shareconnect.qbitconnect.data

import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.Settings
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PreferenceTest {

    private val settings: Settings = MapSettings()

    enum class TestEnum { VALUE1, VALUE2, VALUE3 }

    @Test
    fun `Preference should store and retrieve Int values`() {
        val key = "test_int"
        val initialValue = 42
        val preference = preference(settings, key, initialValue)

        assertEquals(initialValue, preference.value)

        val newValue = 100
        preference.value = newValue

        assertEquals(newValue, preference.value)
        assertEquals(newValue, settings.getInt(key, initialValue))
    }

    @Test
    fun `Preference should store and retrieve Boolean values`() {
        val key = "test_bool"
        val initialValue = true
        val preference = preference(settings, key, initialValue)

        assertEquals(initialValue, preference.value)

        val newValue = false
        preference.value = newValue

        assertEquals(newValue, preference.value)
        assertEquals(newValue, settings.getBoolean(key, initialValue))
    }

    @Test
    fun `Preference should store and retrieve String values`() {
        val key = "test_string"
        val initialValue = "default"
        val preference = preference(settings, key, initialValue)

        assertEquals(initialValue, preference.value)

        val newValue = "updated"
        preference.value = newValue

        assertEquals(newValue, preference.value)
        assertEquals(newValue, settings.getString(key, initialValue))
    }

    @Test
    fun `Preference should store and retrieve Float values`() {
        val key = "test_float"
        val initialValue = 3.14f
        val preference = preference(settings, key, initialValue)

        assertEquals(initialValue, preference.value)

        val newValue = 2.71f
        preference.value = newValue

        assertEquals(newValue, preference.value)
        assertEquals(newValue, settings.getFloat(key, initialValue))
    }

    @Test
    fun `Preference should store and retrieve Long values`() {
        val key = "test_long"
        val initialValue = 123456789L
        val preference = preference(settings, key, initialValue)

        assertEquals(initialValue, preference.value)

        val newValue = 987654321L
        preference.value = newValue

        assertEquals(newValue, preference.value)
        assertEquals(newValue, settings.getLong(key, initialValue))
    }

    @Test
    fun `Preference should store and retrieve enum values`() {
        val key = "test_enum"
        val initialValue = TestEnum.VALUE1
        val preference = preference(settings, key, initialValue)

        assertEquals(initialValue, preference.value)

        val newValue = TestEnum.VALUE3
        preference.value = newValue

        assertEquals(newValue, preference.value)
        assertEquals(newValue.name, settings.getString(key, initialValue.name))
    }

    @Test
    fun `Preference should use custom serializer and deserializer`() {
        val key = "test_custom"
        val initialValue = listOf("a", "b", "c")
        val preference = preference(
            settings = settings,
            key = key,
            initialValue = initialValue,
            serializer = { it.joinToString(",") },
            deserializer = { it.split(",").filter { s -> s.isNotEmpty() } }
        )

        assertEquals(initialValue, preference.value)

        val newValue = listOf("x", "y", "z")
        preference.value = newValue

        assertEquals(newValue, preference.value)
        assertEquals("x,y,z", settings.getString(key, ""))
    }

    // TODO: Fix flow testing
    // @Test
    // fun `Preference flow should emit new values`() = runTest {
    //     val key = "test_flow"
    //     val initialValue = "initial"
    //     val preference = preference(settings, key, initialValue)
    //
    //     val collectedValues = mutableListOf<String>()
    //     val job = kotlinx.coroutines.launch {
    //         preference.flow.collect { collectedValues.add(it) }
    //     }
    //
    //     preference.value = "first"
    //     preference.value = "second"
    //
    //     // Wait a bit for flow collection
    //     kotlinx.coroutines.delay(100)
    //
    //     job.cancel()
    //
    //     assertTrue(collectedValues.contains("initial"))
    //     assertTrue(collectedValues.contains("first"))
    //     assertTrue(collectedValues.contains("second"))
    // }

    @Test(expected = UnsupportedOperationException::class)
    fun `Preference should throw exception for unsupported types`() {
        val key = "test_unsupported"
        val initialValue = object {} // Unsupported type

        // This should throw an exception during construction
        preference(settings, key, initialValue)
    }

    @Test
    fun `jsonPreference should serialize and deserialize objects`() {
        @Serializable
        data class TestData(val name: String, val value: Int)

        val key = "test_json"
        val initialValue = TestData("test", 42)
        val preference = jsonPreference(settings, key, initialValue)

        assertEquals(initialValue, preference.value)

        val newValue = TestData("updated", 100)
        preference.value = newValue

        assertEquals(newValue, preference.value)
    }
}