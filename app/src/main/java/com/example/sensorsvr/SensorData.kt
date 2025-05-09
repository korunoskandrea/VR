package com.example.sensorsvr

import java.sql.Timestamp

data class SensorData(
    val x: Float,
    val y: Float,
    val z: Float,
    val sensorType: String,
    val timestamp: Long
)
