package com.kimmandoo.mqtt.paho

import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class MqttClientManager(private val serverUri: String, private val clientId: String) {

    private lateinit var mqttClient: MqttClient

    fun connect() {
        try {
            mqttClient = MqttClient(serverUri, clientId, MemoryPersistence())
            val options = MqttConnectOptions()
            options.isCleanSession = true
            mqttClient.connect(options)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun publish(topic: String, msg: String) {
        try {
            val message = MqttMessage(msg.toByteArray())
            mqttClient.publish(topic, message)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun subscribe(topic: String) {
        try {
            mqttClient.subscribe(topic)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun setCallback(callback: MqttCallback) {
        mqttClient.setCallback(callback)
    }

    fun disconnect() {
        try {
            mqttClient.disconnect()
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }
}