package com.example.sensorsvr.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.sensorsvr.R
import com.example.sensorsvr.model.PredictionResultData
import com.example.sensorsvr.model.SensorData
import com.example.sensorsvr.model.StatsData
import com.example.sensorsvr.ui.navigation.BottomNavigationBar
import com.example.sensorsvr.ui.navigation.TopNavBar
import com.example.sensorsvr.utils.calculateAccuracy
import com.example.sensorsvr.utils.calculateConfusionMatrix
import com.example.sensorsvr.utils.calculatePrecision
import com.example.sensorsvr.utils.getBottomNavigationTabs
import com.example.sensorsvr.viewModel.DataViewModel
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun AnalysisWidget(
    navController: NavController,
    dataViewModel: DataViewModel = viewModel()
) {
    val isHistory by dataViewModel.isHistory
    val data by dataViewModel.data
    val username by dataViewModel.username
    val trueLabel by dataViewModel.trueLabel

    val (accelData, gyroData) = remember(data) {
        Pair(
            data.filter { it.sensorType == "accelerometer" },
            data.filter { it.sensorType == "gyroscope" }
        )
    }

    val result = remember(data) { analyzeMovement(data) }
    val isCorrect = result == trueLabel
    val testResults = remember { listOf(PredictionResultData(trueLabel, result)) }
    val accuracy = calculateAccuracy(testResults)
    val matrix = calculateConfusionMatrix(testResults)

    val animationRes = when (result) {
        "Walking up the stairs" -> R.raw.up
        "Walking down the stairs" -> R.raw.down
        else -> R.raw.walk
    }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animationRes))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Scaffold(
        topBar = { TopNavBar(navController = navController, dataViewModel) },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                tabs = getBottomNavigationTabs(isHistory)
            )
        }
    ) { paddingValues ->
        if (data.isEmpty()) {
            EmptyStateScreen(paddingValues)
        } else {
            AnalysisContent(
                paddingValues = paddingValues,
                username = username,
                trueLabel = trueLabel,
                result = result,
                isCorrect = isCorrect,
                accuracy = accuracy,
                matrix = matrix,
                accelData = accelData,
                gyroData = gyroData
            )
        }
    }
}

@Composable
private fun EmptyStateScreen(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No data available for analysis",
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@Composable
private fun AnalysisContent(
    paddingValues: PaddingValues,
    username: String,
    trueLabel: String,
    result: String,
    isCorrect: Boolean,
    accuracy: Double,
    matrix: Map<Pair<String, String>, Int>,
    accelData: List<SensorData>,
    gyroData: List<SensorData>,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        item { UserHeader(username) }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item { PredictionResultCard(trueLabel, result, isCorrect) }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item { AccuracyCard(accuracy) }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item { PrecisionCard(trueLabel, result) }  // New precision card
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item { ConfusionMatrixCard(matrix) }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item { SensorStatsSection(accelData, gyroData) }
    }
}

@Composable
private fun UserHeader(username: String) {
    Text(
        text = "User: $username",
        style = MaterialTheme.typography.headlineSmall,
    )
}

@Composable
private fun PredictionResultCard(trueLabel: String, result: String, isCorrect: Boolean) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Movement Analysis",
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("True movement: $trueLabel")
            Text("Detected movement: $result")
            Text(
                text = if (isCorrect) "✅ Correct prediction" else "❌ Incorrect prediction",
                color = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun AccuracyCard(accuracy: Double) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Model Performance",
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Accuracy: ${"%.1f".format(accuracy * 100)}%")
        }
    }
}

@Composable
private fun ConfusionMatrixCard(matrix: Map<Pair<String, String>, Int>) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Confusion Matrix",
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(8.dp))
            matrix.forEach { (key, count) ->
                Text("True: ${key.first}, Predicted: ${key.second} ")
            }
        }
    }
}

@Composable
private fun PrecisionCard(trueLabel: String, predictedLabel: String) {
    val testResults = listOf(PredictionResultData(trueLabel, predictedLabel))
    val precision = calculatePrecision(predictedLabel, testResults)

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Precision",
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("For '${predictedLabel}': ${"%.1f".format(precision * 100)}%")
        }
    }
}

@Composable
private fun SensorStatsSection(accelData: List<SensorData>, gyroData: List<SensorData>) {
    val accelStats = computeStats(accelData.map { it.z.toDouble() })
    val gyroStats = computeStats(gyroData.map {
        sqrt((it.x * it.x + it.y * it.y + it.z * it.z).toDouble())
    })

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Sensor Statistics",
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Accelerometer (Z-axis)")
            Text("Min: %.2f".format(accelStats.min))
            Text("Max: %.2f".format(accelStats.max))
            Text("Avg: %.2f".format(accelStats.avg))
            Text("StdDev: %.2f".format(accelStats.stdDev))

            Spacer(modifier = Modifier.height(16.dp))

            Text("Gyroscope (Magnitude)")
            Text("Min: %.2f".format(gyroStats.min))
            Text("Max: %.2f".format(gyroStats.max))
            Text("Avg: %.2f".format(gyroStats.avg))
            Text("StdDev: %.2f".format(gyroStats.stdDev))
        }
    }
}

fun analyzeMovement(data: List<SensorData>): String {
    if (data.size < 20) return "Insufficient data for analysis"

    val accelData = data.filter { it.sensorType == "accelerometer" }
    val gyroData = data.filter { it.sensorType == "gyroscope" }

    // Calculate accelerometer Z-axis statistics
    val zValues = accelData.map { it.z }
    val avgZ = zValues.average()
    val stdDevZ = sqrt(zValues.map { (it - avgZ).pow(2) }.average())

    // Calculate gyroscope magnitude statistics
    val gyroMagnitude = gyroData.map { sqrt(it.x.pow(2) + it.y.pow(2) + it.z.pow(2)) }
    val avgGyro = gyroMagnitude.average()

    return when {
        stdDevZ > 1.8 && avgGyro > 1.5 -> "Up"
        stdDevZ > 1.2 && avgGyro > 1.0 -> "Down"
        else -> "Straight"
    }
}

fun computeStats(values: List<Double>): StatsData {
    if (values.isEmpty()) return StatsData(0.0, 0.0, 0.0, 0.0)

    val avg = values.average()
    val variance = values.map { (it - avg).pow(2) }.average()
    return StatsData(
        min = values.minOrNull() ?: 0.0,
        max = values.maxOrNull() ?: 0.0,
        avg = avg,
        stdDev = sqrt(variance)
    )
}