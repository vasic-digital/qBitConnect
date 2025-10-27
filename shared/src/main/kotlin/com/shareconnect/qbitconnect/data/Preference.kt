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

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class Preference<T : Any>(
    private val settings: Settings,
    private val key: String,
    private val initialValue: T,
    private val type: KClass<T>,
    private val enumValues: Array<T>? = null,
    private val serializer: ((T) -> String)? = null,
    private val deserializer: ((String) -> T)? = null,
) {
    var value: T
        get() {
            if (serializer != null && deserializer != null) {
                val raw = settings[key, serializer(initialValue)]
                return deserializer(raw)
            }

            return when {
                enumValues != null -> enumValues.find {
                    (it as Enum<*>).name == settings[key, (initialValue as Enum<*>).name]
                } ?: initialValue
                type == Int::class -> settings[key, initialValue as Int]
                type == Boolean::class -> settings[key, initialValue as Boolean]
                type == Float::class -> settings[key, initialValue as Float]
                type == Long::class -> settings[key, initialValue as Long]
                type == String::class -> settings[key, initialValue as String]
                else -> throw UnsupportedOperationException("${type.simpleName} is not supported in Preference")
            } as T
        }

        set(value) {
            if (serializer != null && deserializer != null) {
                settings[key] = serializer(value)
            } else {
                when {
                    enumValues != null -> settings[key] = (value as Enum<*>).name
                    type == Int::class -> settings[key] = value as Int
                    type == Boolean::class -> settings[key] = value as Boolean
                    type == Float::class -> settings[key] = value as Float
                    type == Long::class -> settings[key] = value as Long
                    type == String::class -> settings[key] = value as String
                    else -> throw UnsupportedOperationException("${type.simpleName} is not supported in Preference")
                }
            }
            flow.value = value
        }

    val flow = MutableStateFlow(value)
}

inline fun <reified T : Any> preference(
    settings: Settings,
    key: String,
    initialValue: T,
    noinline serializer: ((T) -> String)? = null,
    noinline deserializer: ((String) -> T)? = null,
) = Preference(settings, key, initialValue, T::class, null, serializer, deserializer)

inline fun <reified T : Enum<T>> preference(settings: Settings, key: String, initialValue: T) =
    Preference(settings, key, initialValue, T::class, enumValues<T>())

val preferenceJson = Json {
    ignoreUnknownKeys = true
}

inline fun <reified T : Any> jsonPreference(
    settings: Settings,
    key: String,
    initialValue: T,
    serializer: KSerializer<T> = serializer<T>(),
) = Preference(
    settings = settings,
    key = key,
    initialValue = initialValue,
    type = T::class,
    enumValues = null,
    serializer = { preferenceJson.encodeToString(serializer, it) },
    deserializer = { if (it.isEmpty()) initialValue else preferenceJson.decodeFromString(serializer, it) },
)