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


package com.shareconnect.qbitconnect.data.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val success: Boolean,
    val message: String? = null
)

@Serializable
data class AddTorrentRequest(
    val urls: List<String>? = null,
    val torrents: List<String>? = null, // base64 encoded
    val savepath: String? = null,
    val cookie: String? = null,
    val category: String? = null,
    val tags: List<String>? = null,
    val skip_checking: Boolean? = null,
    val paused: Boolean? = null,
    val root_folder: Boolean? = null,
    val rename: String? = null,
    val uploadLimit: Long? = null,
    val downloadLimit: Long? = null,
    val ratioLimit: Float? = null,
    val seedingTimeLimit: Long? = null,
    val autoTMM: Boolean? = null,
    val sequentialDownload: Boolean? = null,
    val firstLastPiecePrio: Boolean? = null
)

@Serializable
data class TorrentActionRequest(
    val hashes: List<String>,
    val deleteFiles: Boolean? = null
)

@Serializable
data class SetCategoryRequest(
    val hashes: List<String>,
    val category: String
)

@Serializable
data class SetTagsRequest(
    val hashes: List<String>,
    val tags: List<String>
)

@Serializable
data class SetLimitsRequest(
    val hashes: List<String>,
    val downloadLimit: Long? = null,
    val uploadLimit: Long? = null
)