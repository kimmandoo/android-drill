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
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish
import com.hivemq.client.mqtt.mqtt3.message.subscribe.Mqtt3Subscribe
import com.hivemq.client.mqtt.mqtt5.advanced.interceptor.qos1.Mqtt5IncomingQos1Interceptor
import com.kimmandoo.mqtt.R

private const val TAG = "MQTT3QoSActivity"

class MQTT3QoSActivity : AppCompatActivity() {
    private lateinit var client: Mqtt3AsyncClient
    private val MQTT_SERVER = "192.168.100.199"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mqtt3_qo_sactivity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        client = MqttClient.builder()
            .useMqttVersion3()
            .identifier("my-mqtt-client-id")
            .serverHost(MQTT_SERVER)
            .serverPort(1883)
            .buildAsync()

        client.connect().whenComplete { connAck, throwable ->
            if (throwable != null) {
                Log.e(TAG, "Connection failed", throwable)
                return@whenComplete
            }
            Log.d(TAG, "Connected successfully")

            // Subscribe to a topic with QoS 1
            val subscribe = Mqtt3Subscribe.builder()
                .topicFilter("pub/topic")
                .qos(com.hivemq.client.mqtt.mqtt3.message.subscribe.Mqtt3Subscription.DEFAULT_QOS)
                .build()

            client.subscribe(subscribe) { publish: Mqtt3Publish ->
                // Handle incoming messages
                val message = String(publish.payloadAsBytes)
                Log.d(TAG, "Received message: $message")
            }.whenComplete { subAck, throwable ->
                if (throwable != null) {
                    Log.e(TAG, "Subscription failed", throwable)
                } else {
                    Log.d(TAG, "Subscribed successfully")
                }
            }
        }

        findViewById<TextView>(R.id.hive).setOnClickListener {
            // Publish a message with QoS 2
            val publish = Mqtt3Publish.builder()
                .topic("sub/topic")
                .payload("Hello, MQTT!".toByteArray())
                .build()

            client.publish(publish).whenComplete { result, throwable ->
                if (throwable != null) {
                    Log.e(TAG, "Publishing failed", throwable)
                } else {
                    Log.d(TAG, "Published successfully")
                }
            }
        }
    }
}