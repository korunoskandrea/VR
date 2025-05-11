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
                Text("Ni podatkov za prikaz.")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Text(
                    "Analiza za uporabnika: $username",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Rezultat: $result",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

fun analyzeMovement(data: List<SensorData>): String {
    val accelData = data.filter { it.sensorType == "accelerometer" }
    if (accelData.size < 10) return "Premalo podatkov za analizo"

    // Izračunamo standardni odklon Z-osi
    val zValues = accelData.map { it.z }
    val avg = zValues.average()
    val stdDev = kotlin.math.sqrt(zValues.map { (it - avg) * (it - avg) }.average())

    return when {
        stdDev > 1.8 -> "Hod po stopnicah navzgor"
        stdDev > 1.2 -> "Hod po stopnicah navzdol"
        else -> "Hod po ravnem"
    }
}