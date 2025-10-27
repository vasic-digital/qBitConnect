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

class RequestResultTest {

    @Test
    fun `Success should contain data`() {
        val data = "test data"
        val result = RequestResult.Success(data)

        assertTrue(result is RequestResult.Success)
        assertEquals(data, (result as RequestResult.Success).data)
    }

    @Test
    fun `Error RequestError Banned should be error type`() {
        val result = RequestResult.Error.RequestError.Banned

        assertTrue(result is RequestResult.Error)
        assertTrue(result is RequestResult.Error.RequestError.Banned)
    }

    @Test
    fun `Error RequestError InvalidCredentials should be error type`() {
        val result = RequestResult.Error.RequestError.InvalidCredentials

        assertTrue(result is RequestResult.Error)
        assertTrue(result is RequestResult.Error.RequestError.InvalidCredentials)
    }

    @Test
    fun `Error RequestError UnknownLoginResponse should contain response`() {
        val response = "unknown response"
        val result = RequestResult.Error.RequestError.UnknownLoginResponse(response)

        assertTrue(result is RequestResult.Error)
        assertTrue(result is RequestResult.Error.RequestError.UnknownLoginResponse)
        assertEquals(response, (result as RequestResult.Error.RequestError.UnknownLoginResponse).response)
    }

    @Test
    fun `Error ApiError should contain code`() {
        val code = 404
        val result = RequestResult.Error.ApiError(code)

        assertTrue(result is RequestResult.Error)
        assertTrue(result is RequestResult.Error.ApiError)
        assertEquals(code, (result as RequestResult.Error.ApiError).code)
    }

    @Test
    fun `Error NetworkError should contain throwable`() {
        val exception = RuntimeException("network error")
        val result = RequestResult.Error.NetworkError(exception)

        assertTrue(result is RequestResult.Error)
        assertTrue(result is RequestResult.Error.NetworkError)
        assertEquals(exception, (result as RequestResult.Error.NetworkError).throwable)
    }
}