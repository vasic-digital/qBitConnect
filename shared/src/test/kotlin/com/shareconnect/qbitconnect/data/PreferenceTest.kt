package com.shareconnect.qbitconnect.data

import com.russhwolf.settings.MockSettings
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class PreferenceTest {

    private lateinit var settings: MockSettings

    @Before
    fun setup() {
        settings = MockSettings()
    }

    @Test
    fun `string preference should store and retrieve values`() {
        val pref = preference(settings, "test_string", "default")

        // Initial value should be default
        assertEquals("default", pref.value)

        // Setting new value
        pref.value = "new_value"
        assertEquals("new_value", pref.value)

        // Flow should emit new value
        assertEquals("new_value", pref.flow.value)
    }

    @Test
    fun `int preference should store and retrieve values`() {
        val pref = preference(settings, "test_int", 42)

        assertEquals(42, pref.value)

        pref.value = 100
        assertEquals(100, pref.value)
        assertEquals(100, pref.flow.value)
    }

    @Test
    fun `boolean preference should store and retrieve values`() {
        val pref = preference(settings, "test_bool", false)

        assertEquals(false, pref.value)

        pref.value = true
        assertEquals(true, pref.value)
        assertEquals(true, pref.flow.value)
    }

    @Test
    fun `float preference should store and retrieve values`() {
        val pref = preference(settings, "test_float", 3.14f)

        assertEquals(3.14f, pref.value)

        pref.value = 2.71f
        assertEquals(2.71f, pref.value)
        assertEquals(2.71f, pref.flow.value)
    }

    @Test
    fun `long preference should store and retrieve values`() {
        val pref = preference(settings, "test_long", 123456789L)

        assertEquals(123456789L, pref.value)

        pref.value = 987654321L
        assertEquals(987654321L, pref.value)
        assertEquals(987654321L, pref.flow.value)
    }

    enum class TestEnum { OPTION_A, OPTION_B, OPTION_C }

    @Test
    fun `enum preference should store and retrieve values`() {
        val pref = preference(settings, "test_enum", TestEnum.OPTION_A)

        assertEquals(TestEnum.OPTION_A, pref.value)

        pref.value = TestEnum.OPTION_B
        assertEquals(TestEnum.OPTION_B, pref.value)
        assertEquals(TestEnum.OPTION_B, pref.flow.value)
    }

    @Test
    fun `enum preference should handle missing values gracefully`() {
        // Set an enum value first
        val pref = preference(settings, "test_enum", TestEnum.OPTION_A)
        pref.value = TestEnum.OPTION_B

        // Manually set an invalid enum name in settings
        settings.putString("test_enum", "INVALID_OPTION")

        // Should return default value when enum not found
        assertEquals(TestEnum.OPTION_A, pref.value)
    }

    @Test
    fun `custom serializer preference should work`() {
        val serializer = { value: List<String> -> value.joinToString(",") }
        val deserializer = { raw: String -> if (raw.isEmpty()) emptyList() else raw.split(",") }

        val pref = preference(settings, "test_list", listOf("a", "b"), serializer, deserializer)

        assertEquals(listOf("a", "b"), pref.value)

        pref.value = listOf("x", "y", "z")
        assertEquals(listOf("x", "y", "z"), pref.value)
        assertEquals(listOf("x", "y", "z"), pref.flow.value)
    }

    @Serializable
    data class TestData(val name: String, val value: Int)

    @Test
    fun `json preference should serialize and deserialize objects`() {
        val initialData = TestData("test", 42)
        val pref = jsonPreference(settings, "test_json", initialData)

        assertEquals(initialData, pref.value)

        val newData = TestData("updated", 100)
        pref.value = newData
        assertEquals(newData, pref.value)
        assertEquals(newData, pref.flow.value)
    }

    @Test
    fun `json preference should handle empty string gracefully`() {
        val initialData = TestData("default", 0)
        val pref = jsonPreference(settings, "test_json", initialData)

        // Manually set empty string in settings
        settings.putString("test_json", "")

        // Should return initial value when empty
        assertEquals(initialData, pref.value)
    }

    @Test
    fun `unsupported type should throw exception`() {
        // Create a preference with an unsupported type manually
        assertFailsWith<UnsupportedOperationException> {
            val pref = Preference(
                settings = settings,
                key = "test",
                initialValue = Any(),
                type = Any::class,
                enumValues = null,
                serializer = null,
                deserializer = null
            )
            pref.value // This should trigger the exception
        }
    }

    @Test
    fun `preference should update flow when value changes`() = runTest {
        val pref = preference(settings, "test_flow", "initial")

        // Initial flow value
        assertEquals("initial", pref.flow.value)

        // Update value
        pref.value = "updated"

        // Flow should be updated
        assertEquals("updated", pref.flow.value)
    }

    @Test
    fun `preference should persist values across instances`() {
        val pref1 = preference(settings, "persistent", "value1")
        pref1.value = "persisted"

        // Create new instance with same key
        val pref2 = preference(settings, "persistent", "default")

        // Should retrieve persisted value
        assertEquals("persisted", pref2.value)
    }

    @Test
    fun `preferenceJson should ignore unknown keys`() {
        val json = preferenceJson
        assertTrue(json.configuration.ignoreUnknownKeys)
    }

    @Test
    fun `different preference types should not interfere`() {
        val stringPref = preference(settings, "string_key", "default")
        val intPref = preference(settings, "int_key", 0)
        val boolPref = preference(settings, "bool_key", false)

        stringPref.value = "test"
        intPref.value = 42
        boolPref.value = true

        assertEquals("test", stringPref.value)
        assertEquals(42, intPref.value)
        assertEquals(true, boolPref.value)
    }
}