package com.shareconnect.qbitconnect.network

import com.shareconnect.qbitconnect.model.RequestResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RequestUtilsTest {

    @Test
    fun `catchRequestError should return success when block succeeds`() = runTest {
        val expectedData = "success data"
        val result = catchRequestError<String>(
            block = { RequestResult.Success(expectedData) }
        )

        assertTrue(result is RequestResult.Success)
        assertEquals(expectedData, (result as RequestResult.Success).data)
    }

    @Test
    fun `catchRequestError should return NetworkError when block throws exception`() = runTest {
        val exception = RuntimeException("test exception")
        val result = catchRequestError<String>(
            block = { throw exception }
        )

        assertTrue(result is RequestResult.Error.NetworkError)
        assertEquals(exception, (result as RequestResult.Error.NetworkError).throwable)
    }

    @Test
    fun `catchRequestError should execute finally block when block succeeds`() = runTest {
        var finallyExecuted = false
        val result = catchRequestError<String>(
            block = { RequestResult.Success("data") },
            finally = { finallyExecuted = true }
        )

        assertTrue(result is RequestResult.Success)
        assertTrue(finallyExecuted)
    }

    @Test
    fun `catchRequestError should execute finally block when block throws exception`() = runTest {
        var finallyExecuted = false
        val result = catchRequestError<String>(
            block = { throw RuntimeException("error") },
            finally = { finallyExecuted = true }
        )

        assertTrue(result is RequestResult.Error.NetworkError)
        assertTrue(finallyExecuted)
    }

    @Test(expected = RuntimeException::class)
    fun `catchRequestError should execute finally block even when finally throws exception`() = runTest {
        catchRequestError<String>(
            block = { RequestResult.Success("data") },
            finally = {
                throw RuntimeException("finally error")
            }
        )
    }
}