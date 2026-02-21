package com.doctorlasya.data.api

import com.doctorlasya.data.models.GeminiRequest
import com.doctorlasya.data.models.GeminiStreamChunk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class GeminiApiService @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val json: Json,
    @Named("gemini_api_key") private val apiKey: String
) {
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta"
    private val model   = "gemini-2.0-flash-exp"

    /**
     * 🌊 Stream chat response — Server-Sent Events (SSE)
     * This is the heart of our low-latency architecture.
     * Each chunk is emitted as soon as it arrives from Gemini.
     */
    fun streamChat(request: GeminiRequest): Flow<GeminiStreamChunk> = flow {
        val url = "$baseUrl/models/$model:streamGenerateContent?key=$apiKey&alt=sse"
        val requestBody = json.encodeToString(GeminiRequest.serializer(), request)
            .toRequestBody("application/json".toMediaType())

        val httpRequest = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        okHttpClient.newCall(httpRequest).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Gemini API error: ${response.code}")
            }

            response.body?.source()?.let { source ->
                while (!source.exhausted()) {
                    val line = source.readUtf8Line() ?: break

                    // SSE format: "data: {...json...}"
                    if (line.startsWith("data: ")) {
                        val jsonData = line.removePrefix("data: ").trim()
                        if (jsonData == "[DONE]") break

                        try {
                            val chunk = json.decodeFromString(
                                GeminiStreamChunk.serializer(), jsonData
                            )
                            emit(chunk)
                        } catch (e: Exception) {
                            // Skip malformed chunks gracefully
                        }
                    }
                }
            }
        }
    }
}
