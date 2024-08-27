package com.kimmandoo.websocket

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket


class ROSSocketClient(private val host: String, private val port: Int) {
    private var socket: Socket? = null
    private var inputStream: DataInputStream? = null
    private var outputStream: DataOutputStream? = null

    suspend fun connect() = withContext(Dispatchers.IO) {
        try {
            socket = Socket(host, port)
            inputStream = DataInputStream(socket?.inputStream)
            outputStream = DataOutputStream(socket?.outputStream)
            println("Connected to ROS Master")
        } catch (e: IOException) {
            println("Error connecting to ROS Master: ${e.message}")
        }
    }

    suspend fun sendMessage(message: String) = withContext(Dispatchers.IO) {
        try {
            outputStream?.writeUTF(message)
            outputStream?.flush()
        } catch (e: IOException) {
            println("Error sending message: ${e.message}")
        }
    }

    suspend fun receiveMessage(): String = withContext(Dispatchers.IO) {
        try {
            return@withContext inputStream?.readUTF() ?: ""
        } catch (e: IOException) {
            println("Error receiving message: ${e.message}")
            return@withContext ""
        }
    }

    fun close() {
        try {
            socket?.close()
            inputStream?.close()
            outputStream?.close()
        } catch (e: IOException) {
            println("Error closing connection: ${e.message}")
        }
    }
}