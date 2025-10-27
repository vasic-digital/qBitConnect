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


package com.shareconnect.qbitconnect.network

import com.shareconnect.qbitconnect.model.RequestResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class RequestUtilsTest {

    @Test
    fun `catchRequestError should return success when block succeeds`() = runTest {
        val expectedResult = RequestResult.Success("test data")

        val result = catchRequestError(
            block = { expectedResult }
        )

        assertEquals(expectedResult, result)
    }

    @Test
    fun `catchRequestError should return error when block returns error`() = runTest {
        val expectedError = RequestResult.Error.RequestError.InvalidCredentials

        val result = catchRequestError(
            block = { expectedError }
        )

        assertEquals(expectedError, result)
    }

    @Test
    fun `catchRequestError should handle IOException as NetworkError`() = runTest {
        val ioException = IOException("Network error")

        val result = catchRequestError(
            block = { throw ioException }
        )

        assertTrue(result is RequestResult.Error.NetworkError)
        assertEquals(ioException, (result as RequestResult.Error.NetworkError).exception)
    }

    @Test
    fun `catchRequestError should handle UnknownHostException as NetworkError`() = runTest {
        val unknownHostException = UnknownHostException("Host not found")

        val result = catchRequestError(
            block = { throw unknownHostException }
        )

        assertTrue(result is RequestResult.Error.NetworkError)
        assertEquals(unknownHostException, (result as RequestResult.Error.NetworkError).exception)
    }

    @Test
    fun `catchRequestError should handle ConnectException as NetworkError`() = runTest {
        val connectException = ConnectException("Connection refused")

        val result = catchRequestError(
            block = { throw connectException }
        )

        assertTrue(result is RequestResult.Error.NetworkError)
        assertEquals(connectException, (result as RequestResult.Error.NetworkError).exception)
    }

    @Test
    fun `catchRequestError should handle SocketTimeoutException as NetworkError`() = runTest {
        val timeoutException = SocketTimeoutException("Request timeout")

        val result = catchRequestError(
            block = { throw timeoutException }
        )

        assertTrue(result is RequestResult.Error.NetworkError)
        assertEquals(timeoutException, (result as RequestResult.Error.NetworkError).exception)
    }

    @Test
    fun `catchRequestError should handle RuntimeException as NetworkError`() = runTest {
        val runtimeException = RuntimeException("Unexpected error")

        val result = catchRequestError(
            block = { throw runtimeException }
        )

        assertTrue(result is RequestResult.Error.NetworkError)
        assertEquals(runtimeException, (result as RequestResult.Error.NetworkError).exception)
    }

    @Test
    fun `catchRequestError should rethrow CancellationException`() = runTest {
        assertFailsWith<CancellationException> {
            catchRequestError(
                block = { throw CancellationException("Cancelled") }
            )
        }
    }

    @Test
    fun `catchRequestError should execute finally block on success`() = runTest {
        var finallyExecuted = false

        val result = catchRequestError(
            block = { RequestResult.Success("test") },
            finally = { finallyExecuted = true }
        )

        assertTrue(result is RequestResult.Success)
        assertTrue(finallyExecuted)
    }

    @Test
    fun `catchRequestError should execute finally block on exception`() = runTest {
        var finallyExecuted = false

        val result = catchRequestError(
            block = { throw IOException("Error") },
            finally = { finallyExecuted = true }
        )

        assertTrue(result is RequestResult.Error.NetworkError)
        assertTrue(finallyExecuted)
    }

    @Test
    fun `catchRequestError should execute finally block even when it throws`() = runTest {
        var finallyExecuted = false

        val result = catchRequestError(
            block = { RequestResult.Success("test") },
            finally = {
                finallyExecuted = true
                throw RuntimeException("Finally error")
            }
        )

        assertTrue(result is RequestResult.Success)
        assertTrue(finallyExecuted)
    }

    @Test
    fun `catchRequestError should execute finally block when CancellationException is thrown`() = runTest {
        var finallyExecuted = false

        assertFailsWith<CancellationException> {
            catchRequestError(
                block = { throw CancellationException("Cancelled") },
                finally = { finallyExecuted = true }
            )
        }

        assertTrue(finallyExecuted)
    }

    @Test
    fun `catchRequestError should preserve different RequestResult Error types`() = runTest {
        val apiError = RequestResult.Error.ApiError(500)

        val result = catchRequestError(
            block = { apiError }
        )

        assertEquals(apiError, result)
    }
}