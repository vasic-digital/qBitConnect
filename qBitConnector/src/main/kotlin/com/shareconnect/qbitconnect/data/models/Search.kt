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

data class SearchPlugin(
    val name: String,
    val fullName: String,
    val url: String,
    val enabled: Boolean = true,
    val version: String = "",
    val supportedCategories: List<String> = emptyList()
)

data class SearchQuery(
    val pattern: String,
    val category: String = "all",
    val plugins: List<String> = emptyList()
)

data class SearchResult(
    val fileName: String,
    val fileSize: Long,
    val nbSeeders: Int,
    val nbLeechers: Int,
    val fileUrl: String,
    val descrLink: String = "",
    val engineName: String = ""
)

data class SearchResults(
    val results: List<SearchResult> = emptyList(),
    val status: String = "Running",  // "Running" or "Stopped"
    val total: Int = 0
)