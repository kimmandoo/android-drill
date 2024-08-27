package com.kimmandoo.websocket

import android.os.Handler
import android.os.Looper
import okhttp3.*
import java.util.concurrent.TimeUnit

class WebSocketClient(private val url: String) {

    private var webSocket: WebSocket? = null
    private val client: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    private val mainHandler = Handler(Looper.getMainLooper())

    var onMessageReceived: ((String) -> Unit)? = null
    var onConnectionOpened: (() -> Unit)? = null
    var onConnectionClosed: ((code: Int, reason: String) -> Unit)? = null
    var onConnectionFailed: ((Throwable) -> Unit)? = null

    fun connect() {
        val request = Request.Builder()
            .url(url)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                mainHandler.post {
                    onConnectionOpened?.invoke()
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                mainHandler.post {
                    onMessageReceived?.invoke(text)
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                mainHandler.post {
                    onConnectionClosed?.invoke(code, reason)
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                mainHandler.post {
                    onConnectionFailed?.invoke(t)
                }
            }
        })
    }

    fun sendMessage(message: String) {
        webSocket?.send(message)
    }

    fun disconnect() {
        webSocket?.close(1000, "사용자가 연결을 종료함")
    }
}