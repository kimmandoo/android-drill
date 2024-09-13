package com.kimmandoo.mqtt.hiveMQ

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import com.kimmandoo.mqtt.R

private const val TAG = "HiveActivity"

class HiveActivity : AppCompatActivity() {
    private lateinit var client: Mqtt3AsyncClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_hive)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val MQTT_SERVER = "192.168.100.199"

        client = MqttClient.builder()
            .useMqttVersion3()
            .identifier("my-mqtt-client-id")
            .serverHost(MQTT_SERVER)
            .serverPort(1883)
            .buildAsync()

        client.connect().whenComplete { connAck, throwable ->
            if (throwable != null) {
                // Handle connection failure
                return@whenComplete
            }

            // Subscribe to a topic
            client.subscribeWith()
                .topicFilter("pub/topic")
                .callback { publish ->
                    // Handle incoming messages
                    val message = String(publish.payloadAsBytes)
                    println("Received message: $message")
                }
                .send()
        }

        findViewById<TextView>(R.id.hive).setOnClickListener {
            client.publishWith()
                .topic("sub/topic")
                .payload("Hello, MQTT!".toByteArray())
                .send()
                .whenComplete { result, throwable ->
                    if (throwable != null) {
                        // Handle publishing failure
                        Log.d(TAG, "Published throwable")
                        return@whenComplete
                    }
                    // Handle publishing success
                    Log.d(TAG, "Published successfully")
                }

        }

        client.disconnect().whenComplete { connAck, throwable ->
            if (throwable != null) {
                // Handle disconnection failure
                return@whenComplete

                // Handle disconnection success
                Log.d(TAG, "Disconnected successfully")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        client.disconnect()
    }
}