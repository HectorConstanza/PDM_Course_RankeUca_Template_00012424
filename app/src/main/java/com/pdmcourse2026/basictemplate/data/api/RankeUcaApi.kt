package com.pdmcourse2026.basictemplate.data.api

import com.pdmcourse2026.basictemplate.BuildConfig
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable

@Serializable
data class QuestionDto(
    val id: Int,
    val title: String
)

@Serializable
data class OptionDto(
    val id: Int,
    val name: String,
    val imageUrl: String? = null,
    val questionId: Int,
    val votes: Int? = null
)

@Serializable
data class Place(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val votes: Int? = null
)

@Serializable
data class QuestionRequest(val title: String)

@Serializable
data class CreateOptionRequest(
    val name: String,
    val questionId: Int,
    val imageUrl: String? = null
)

@Serializable
data class UpdateOptionRequest(
    val name: String,
    val imageUrl: String? = null
)

class RankeUcaApi(private val baseUrl: String = "https://qjcxdvfzyseuvezacxsd.supabase.co/functions/v1/rankeuca") {
    private val client = KtorClient.client
    private val apiKey = BuildConfig.RANK_UCA_API_KEY

    private fun HttpRequestBuilder.apiConfig() {
        // La API Key va en los query parameters (properties) y headers
        url { parameters.append("apikey", apiKey) }
        header("Authorization", "Bearer $apiKey")
    }

    suspend fun fetchQuestions(): List<QuestionDto> = 
        client.get("$baseUrl/questions") { apiConfig() }.body()

    suspend fun fetchQuestion(id: Int): QuestionDto = 
        client.get("$baseUrl/questions/$id") { apiConfig() }.body()
    
    suspend fun createQuestion(title: String): QuestionDto = 
        client.post("$baseUrl/questions") {
            apiConfig()
            contentType(ContentType.Application.Json)
            setBody(QuestionRequest(title))
        }.body()

    suspend fun updateQuestion(id: Int, title: String) {
        client.put("$baseUrl/questions/$id") {
            apiConfig()
            contentType(ContentType.Application.Json)
            setBody(QuestionRequest(title))
        }
    }

    suspend fun deleteQuestion(id: Int) {
        client.delete("$baseUrl/questions/$id") { apiConfig() }
    }

    suspend fun fetchOptions(): List<OptionDto> = 
        client.get("$baseUrl/options") { apiConfig() }.body()

    suspend fun createOption(questionId: Int, name: String, imageUrl: String? = null): OptionDto =
        client.post("$baseUrl/options") {
            apiConfig()
            contentType(ContentType.Application.Json)
            setBody(CreateOptionRequest(name, questionId, imageUrl))
        }.body()

    suspend fun updateOption(id: Int, name: String, imageUrl: String? = null) {
        client.put("$baseUrl/options/$id") {
            apiConfig()
            contentType(ContentType.Application.Json)
            setBody(UpdateOptionRequest(name, imageUrl))
        }
    }

    suspend fun deleteOption(id: Int) {
        client.delete("$baseUrl/options/$id") { apiConfig() }
    }
}
