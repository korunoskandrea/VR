package com.example.sensorsvr.utils

import com.example.sensorsvr.model.PredictionResult

fun calculateConfusionMatrix(results: List<PredictionResult>): Map<Pair<String, String>, Int> {
    val matrix = mutableMapOf<Pair<String, String>, Int>()

    for (res in results) {
        val key = Pair(res.trueLabel, res.predictedLabel)
        matrix[key] = matrix.getOrDefault(key, 0) + 1
    }
    return matrix
}

fun calculateAccuracy(results: List<PredictionResult>): Double {
    return (results.count() { it.trueLabel == it.predictedLabel }.toDouble()) / results.size
}

fun calculatePrecision(label: String, results: List<PredictionResult>): Double {
    val tp = results.count() { it.predictedLabel == label && it.trueLabel == label }
    val fp = results.count() { it.predictedLabel == label && it.trueLabel != label }

    return if (tp + fp == 0) 0.0 else tp.toDouble() / (tp + fp)
}