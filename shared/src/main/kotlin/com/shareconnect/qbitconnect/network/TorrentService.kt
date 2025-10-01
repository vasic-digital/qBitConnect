package com.shareconnect.qbitconnect.network

import io.ktor.client.HttpClient
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.parameters

class TorrentService(
    private val client: HttpClient,
    private val baseUrl: String
) {
    suspend fun getVersion(): Response<String> {
        return try {
            val response = client.get("$baseUrl/api/v2/app/version")
            Response(response.status.value, response.bodyAsText())
        } catch (e: Exception) {
            Response(0, null)
        }
    }

    suspend fun login(username: String, password: String): Response<String> {
        return try {
            val response = client.submitForm(
                url = "$baseUrl/api/v2/auth/login",
                formParameters = parameters {
                    append("username", username)
                    append("password", password)
                }
            )
            Response(response.status.value, response.bodyAsText())
        } catch (e: Exception) {
            Response(0, null)
        }
    }
}