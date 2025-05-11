package com.example.sensorsvr.ui.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sensorsvr.model.SensorData
import com.example.sensorsvr.ui.navigation.BottomNavigationBar
import com.example.sensorsvr.utils.getBottomNavigationTabs
import com.example.sensorsvr.viewModel.DataViewModel
import kotlin.math.sqrt

@Composable
fun AnalysisWidget(
    navController: NavController,
    dataViewModel: DataViewModel = viewModel()
) {
    val isHistory by dataViewModel.isHistory
    val data by dataViewModel.data
    val username by dataViewModel.username

    val result = remember(data) {
        analyzeMovement(data)
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                tabs = getBottomNavigationTabs(isHistory)
            )
        }
    ) { paddingValues ->
        if (data.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Text("There is no data to be shown.")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Text(
                    "Analysis for the user: $username",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Result: $result",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

fun analyzeMovement(data: List<SensorData>): String {
    val accelData = data.filter { it.sensorType == "accelerometer" }
    val gyroData = data.filter { it.sensorType == "gyroscope" }

    if (accelData.size < 10 || gyroData.size < 10) return "You do not have enough data to analyze the movement."

    // Pospeskometer: standardni odklon Z-osi
    val zValues = accelData.map { it.z }
    val avgZ = zValues.average()
    val stdDevZ = kotlin.math.sqrt(zValues.map { (it - avgZ) * (it - avgZ) }.average())

    // Ziroskop: povprecna "rotacijska energija"
    val gyroMagnitude =
        gyroData.map { sqrt((it.x * it.x + it.y * it.y + it.z * it.z).toDouble()) }
    val avgGyro = gyroMagnitude.average()

    return when {
        stdDevZ > 1.8 && avgGyro > 1.5 -> "Walking up the stairs"
        stdDevZ > 1.2 && avgGyro > 1.0 -> "Walking down the stairs"
        else -> "Straight walk"
    }
}
