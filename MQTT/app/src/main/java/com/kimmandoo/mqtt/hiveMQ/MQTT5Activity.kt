package com.kimmandoo.mqtt.hiveMQ

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish
import com.kimmandoo.mqtt.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MQTT5Activity : AppCompatActivity() {

    private lateinit var client: Mqtt5AsyncClient
    private val MQTT_SERVER = "192.168.100.199"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mqtt5)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupMqttClient()
        findViewById<TextView>(R.id.hive).setOnClickListener {
            publishMessage("GPS")
        }
    }

    private fun setupMqttClient() {
        client = MqttClient.builder()
            .useMqttVersion5()
            .serverHost(MQTT_SERVER)
            .serverPort(1883)
            .buildAsync()

        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    client.connect().whenComplete { _, throwable ->
                        if (throwable != null) {
                            Log.e("MQTT", "Failed to connect", throwable)
                        } else {
                            Log.d("MQTT", "Connected successfully")
                            subscribeToTopic()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("MQTT", "Error setting up MQTT client", e)
            }
        }
    }

    private fun subscribeToTopic() {
        client.subscribeWith()
            .topicFilter("gps/data/v1/publish")
            .callback { publish: Mqtt5Publish ->
                val message = String(publish.payloadAsBytes)
                Log.d("MQTT", "Received message: $message")
            }
            .send()
            .whenComplete { _, throwable ->
                if (throwable != null) {
                    Log.e("MQTT", "Failed to subscribe", throwable)
                } else {
                    Log.d("MQTT", "Subscribed successfully")
                }
            }
    }

    private fun publishMessage(message: String) {
        client.publishWith()
            .topic("gps/data/v1/subscribe")
            .payload(message.toByteArray())
            .send()
            .whenComplete { _, throwable ->
                if (throwable != null) {
                    Log.e("MQTT", "Failed to publish", throwable)
                } else {
                    Log.d("MQTT", "Published successfully")
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        client.disconnect()
    }
}