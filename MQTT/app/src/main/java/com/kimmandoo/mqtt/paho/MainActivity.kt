package com.kimmandoo.mqtt.paho

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kimmandoo.mqtt.R
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var mqttClient: MqttClientManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mqttClient = MqttClientManager(MQTT_SERVER, "AndroidClient")

        mqttClient.connect()

        mqttClient.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable?) {
                // 연결 끊김 처리
                Log.d(TAG, "connectionLost: $cause")
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                // 메시지 수신 처리
                Log.d(TAG, "messageArrived: $topic $message")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                // 메시지 전송 완료 처리
                Log.d(TAG, "deliveryComplete: $token")
            }
        })

        mqttClient.subscribe("pub/topic")

        // 메시지 발행 예
        findViewById<TextView>(R.id.button).setOnClickListener {
            mqttClient.publish("sub/topic", "Hello, MQTT!")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mqttClient.disconnect()
    }

    companion object {
        const val MQTT_SERVER = "tcp://192.168.100.199:1883"
    }
}