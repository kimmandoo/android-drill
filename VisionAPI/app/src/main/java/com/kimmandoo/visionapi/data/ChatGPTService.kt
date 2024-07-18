package com.kimmandoo.visionapi.data

import com.kimmandoo.visionapi.model.Payload
import com.kimmandoo.visionapi.model.VisionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ChatGPTService {
    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    suspend fun getChatCompletion(@Body request: Payload): Response<VisionResponse>
}