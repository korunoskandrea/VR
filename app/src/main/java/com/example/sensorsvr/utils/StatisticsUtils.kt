package com.example.sensorsvr.utils

import com.example.sensorsvr.model.PredictionResultData
import com.example.sensorsvr.model.SensorData
import kotlin.math.pow
import kotlin.math.sqrt

fun calculateConfusionMatrix(results: List<PredictionResultData>): Map<Pair<String, String>, Int> {
    val matrix = mutableMapOf<Pair<String, String>, Int>()

    for (res in results) {
        val key = Pair(res.trueLabel, res.predictedLabel)
        matrix[key] = matrix.getOrDefault(key, 0) + 1
    }
    return matrix
}

fun calculateAccuracy(results: List<PredictionResultData>): Double {
    require(results.isNotEmpty()) { "Cannot calculate accuracy for empty results" }
    return results.count { it.trueLabel == it.predictedLabel }.toDouble() / results.size
}

fun calculatePrecision(label: String, results: List<PredictionResultData>): Double {
    val tp = results.count { it.predictedLabel == label && it.trueLabel == label }
    val fp = results.count { it.predictedLabel == label && it.trueLabel != label }

    return if (tp + fp == 0) 0.0 else tp.toDouble() / (tp + fp)
}

fun analyzeMovement(data: List<SensorData>): String {
    if (data.size < 20) return "Insufficient data for analysis"

    val accelData = data.filter { it.sensorType == "accelerometer" }
    val gyroData = data.filter { it.sensorType == "gyroscope" }

    val zValues = accelData.map { it.z }
    val avgZ = zValues.average()
    val stdDevZ = sqrt(zValues.map { (it - avgZ).pow(2) }.average())

    val gyroMagnitude = gyroData.map { sqrt(it.x.pow(2) + it.y.pow(2) + it.z.pow(2)) }
    val avgGyro = gyroMagnitude.average()

    return when {
        stdDevZ > 0.30 && avgGyro > 0.045 -> "Up"
        stdDevZ > 0.22 && avgGyro > 0.035 -> "Down"
        else -> "Straight"
    }
}