package com.kimmandoo.websocket

import okhttp3.*
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class ROSWebSocketClient(private val url: String) {
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    var onMessageReceived: ((String, String) -> Unit)? = null
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
            val json = JSONObject(text)
            val topic = json.optString("topic")
            val message = json.optJSONObject("msg")?.toString() ?: ""
            onMessageReceived?.invoke(topic, message)
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

    fun subscribe(topic: String) {
        val subscribeMsg = JSONObject().apply {
            put("op", "subscribe")
            put("topic", topic)
        }
        webSocket?.send(subscribeMsg.toString())
    }

    fun publish(topic: String, message: String) {
        val publishMsg = JSONObject().apply {
            put("op", "publish")
            put("topic", topic)
            put("msg", JSONObject(message))
        }
        webSocket?.send(publishMsg.toString())
    }

    fun disconnect() {
        webSocket?.close(1000, "Disconnecting")
    }
}