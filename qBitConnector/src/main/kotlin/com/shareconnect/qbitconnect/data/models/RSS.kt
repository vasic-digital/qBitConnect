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


@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.shareconnect.qbitconnect.data.models

import kotlinx.datetime.Instant

data class RSSFeed(
    val uid: String,
    val url: String,
    val title: String,
    val lastBuildDate: Instant? = null,
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val articles: List<RSSArticle> = emptyList()
)

data class RSSArticle(
    val id: String,
    val title: String,
    val description: String? = null,
    val link: String,
    val author: String? = null,
    val category: String? = null,
    val pubDate: Instant? = null,
    val isRead: Boolean = false,
    val torrentUrl: String? = null,
    val size: Long? = null
)

data class RSSRule(
    val name: String,
    val enabled: Boolean = true,
    val mustContain: String = "",
    val mustNotContain: String = "",
    val useRegex: Boolean = false,
    val episodeFilter: String = "",
    val smartFilter: Boolean = false,
    val previouslyMatchedEpisodes: List<String> = emptyList(),
    val affectedFeeds: List<String> = emptyList(),
    val ignoreDays: Int = 0,
    val lastMatch: Instant? = null,
    val addPaused: Boolean = false,
    val category: String = "",
    val savePath: String = "",
    val downloadLimit: Long = -1,
    val uploadLimit: Long = -1,
    val tags: List<String> = emptyList()
)