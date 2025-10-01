package com.shareconnect.qbitconnect.network

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ResponseTest {

    @Test
    fun `Response should contain code and body`() {
        val code = 200
        val body = "success response"
        val response = Response(code, body)

        assertEquals(code, response.code)
        assertEquals(body, response.body)
    }

    @Test
    fun `Response should handle null body`() {
        val code = 404
        val response = Response(code, null)

        assertEquals(code, response.code)
        assertNull(response.body)
    }

    @Test
    fun `Response should handle different types of body`() {
        val code = 201
        val body = 12345 // Int body
        val response = Response(code, body)

        assertEquals(code, response.code)
        assertEquals(body, response.body)
    }
}