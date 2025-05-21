package com.example.sensorsvr.mqtt

import android.util.Log
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import java.nio.charset.StandardCharsets
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object MqttClientManager {
    private const val TAG = "MQTT"
    private const val BROKER_HOST = "10.104.1.143" // todo change
    private const val BROKER_PORT = 1883
    private const val CLIENT_ID = "AndroidClient_"

    private val mqttClient: Mqtt3AsyncClient = MqttClient.builder()
        .useMqttVersion3()
        .serverHost(BROKER_HOST)
        .serverPort(BROKER_PORT)
        .identifier(CLIENT_ID + UUID.randomUUID().toString())
        .buildAsync()

    @Volatile
    private var isConnected = false

    fun connect() {
        mqttClient.connect().whenComplete { _, throwable ->
            if (throwable != null) {
                Log.e(TAG, "Povezava na MQTT ni uspela: ${throwable.message}")
                isConnected = false
            } else {
                Log.i(TAG, "Povezano na MQTT strežnik.")
                isConnected = true
            }
        }
    }

    fun publish(topic: String, message: String) {
        if (!isConnected) {
            Log.e(TAG, "Objava sporočila ni uspela: MQTT client is not connected.")
            return
        }
        mqttClient.publishWith()
            .topic(topic)
            .payload(message.toByteArray(StandardCharsets.UTF_8))
            .send()
            .whenComplete { _, throwable ->
                if (throwable != null) {
                    Log.e(TAG, "Objava sporočila ni uspela: ${throwable.message}")
                } else {
                    Log.i(TAG, "Sporočilo objavljeno na $topic: $message")
                }
            }
    }

    fun disconnect() {
        mqttClient.disconnect().whenComplete { _, throwable ->
            if (throwable != null) {
                Log.e(TAG, "Odklop ni uspel: ${throwable.message}")
            } else {
                Log.i(TAG, "Odklopljen od MQTT strežnika.")
                isConnected = false
            }
        }
    }

    suspend fun connectSuspend() = suspendCoroutine<Unit> { cont ->
        mqttClient.connect().whenComplete { _, throwable ->
            if (throwable != null) {
                Log.e(TAG, "Povezava na MQTT ni uspela: ${throwable.message}")
                cont.resumeWithException(throwable)
            } else {
                Log.i(TAG, "Povezano na MQTT strežnik.")
                isConnected = true
                cont.resume(Unit)
            }
        }
    }
}