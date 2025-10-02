package com.shareconnect.qbitconnect.data.network

import com.shareconnect.qbitconnect.model.RequestResult
import kotlinx.coroutines.CancellationException

suspend fun <T> catchRequestError(
    block: suspend () -> RequestResult<T>,
    finally: suspend () -> Unit = {}
): RequestResult<T> {
    return try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        RequestResult.Error.NetworkError(e)
    } finally {
        finally()
    }
}