package org.ballistic.dreamjournalai.shared.core.util
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readRawBytes

private val httpClient = HttpClient() // or inject it if you have a global Ktor client

actual suspend fun downloadImageBytes(urlString: String): ByteArray {
    val response: HttpResponse = httpClient.get(urlString)
    return response.readRawBytes()
}