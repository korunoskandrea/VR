package com.example.sensorsvr.model

data class SensorData(
    val x: Float,
    val y: Float,
    val z: Float,
    val sensorType: String,
    val timestamp: Long
)
