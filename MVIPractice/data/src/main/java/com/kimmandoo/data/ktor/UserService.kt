package com.kimmandoo.data.ktor

import android.util.Log
import com.kimmandoo.data.model.CommonResponse
import com.kimmandoo.data.model.LoginRequest
import com.kimmandoo.data.model.SignUpRequest
import com.kimmandoo.data.model.UpdateMyInfoRequest
import com.kimmandoo.data.model.UserResponse
import com.kimmandoo.domain.model.User
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.utils.EmptyContent.headers
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readUTF8Line
import javax.inject.Inject

private const val TAG = "UserService"

class UserService @Inject constructor(
    private val client: HttpClient,
) {
    suspend fun login(requestBody: LoginRequest): CommonResponse<String> {
        Log.d(TAG, "login: $requestBody")
        return client.post("users/login") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }.body()
    }

    suspend fun signup(requestBody: SignUpRequest): CommonResponse<Long> {
        return client.post("users/sign-up") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }.body()
    }

    suspend fun getUserInfo():CommonResponse<UserResponse>{
        return client.get("users/my-page").body()
    }

    suspend fun patchUserInfo(requestBody: UpdateMyInfoRequest):CommonResponse<Long>{
        return client.patch("users/my-page"){
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun getTest() {
        client.get("https://www.thecocktaildb.com/api/json/v1/1/filter.php?c=Ordinary_Drink")
    }

    suspend fun startStreaming() {
        try {
            val response = client.get("http://example.com/streaming-endpoint") {
                headers {
                    append("Authorization", "Bearer YOUR_TOKEN") // 필요 시 인증 헤더 추가
                }
            }

            // ByteReadChannel을 통해 스트리밍 데이터 처리
            val channel: ByteReadChannel = response.body()

            // 채널을 통한 데이터 수신 및 처리
            while (!channel.isClosedForRead) {
                val chunk = channel.readUTF8Line() // 한 줄씩 읽기 (필요에 따라 다른 읽기 메서드 사용 가능)
                if (chunk != null) {
                    processChunk(chunk) // 데이터를 처리하는 함수 호출
                }
            }

        } catch (e: Exception) {
            e.printStackTrace() // 에러 처리
        } finally {
            client.close() // 자원 해제
        }
    }

    // 데이터를 처리하는 함수 예시
    fun processChunk(chunk: String) {
        println("Received chunk: $chunk")
        // UI 업데이트나 다른 데이터 처리 작업 수행
    }
}