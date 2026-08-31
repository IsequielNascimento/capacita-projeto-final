package com.example.capacita_projeto_final.features.sync.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FieldOpsApi {
    @GET("posts/1")
    suspend fun getServiceStatus(): ApiPost

    @POST("posts")
    suspend fun sendVisit(@Body visit: VisitPayload): ApiPost
}

data class ApiPost(
    val id: Int = 0,
    val userId: Int = 0,
    val title: String = "",
    val body: String = "",
)

data class VisitPayload(
    val userId: Int,
    val title: String,
    val body: String,
)
