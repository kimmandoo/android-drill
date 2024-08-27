package com.kimmandoo.websocket

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import okhttp3.*
import java.io.ByteArrayOutputStream

class WebSocketImageClient(private val url: String) {

    private var webSocket: WebSocket? = null
    private val client: OkHttpClient = OkHttpClient.Builder().build()

    var onImageReceived: ((Bitmap) -> Unit)? = null
    var onConnectionOpened: (() -> Unit)? = null
    var onConnectionClosed: ((code: Int, reason: String) -> Unit)? = null
    var onConnectionFailed: ((Throwable) -> Unit)? = null

    fun connect() {
        val request = Request.Builder().url(url).build()
        webSocket = client.newWebSocket(request, createWebSocketListener())
    }

    private fun createWebSocketListener() = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            onConnectionOpened?.invoke()
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            // 수신된 Base64 문자열을 비트맵으로 변환
            // 서버 쪽에서도 Base64로 이미지를 보낸다고 가정한다
            val imageBytes = Base64.decode(text, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            onImageReceived?.invoke(bitmap)
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            webSocket.close(1000, null)
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            onConnectionClosed?.invoke(code, reason)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            onConnectionFailed?.invoke(t)
        }
    }

    fun sendImage(bitmap: Bitmap) {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val imageBytes = outputStream.toByteArray()
        val base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT)

        webSocket?.send(base64Image)
    }

    fun disconnect() {
        webSocket?.close(1000, "사용자가 연결을 종료함")
    }
}