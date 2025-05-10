package com.example.sensorsvr

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.compose.ui.Modifier
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sensorsvr.ui.theme.AllDataScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val sensorViewModel: SensorViewModel = viewModel()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "data",
            modifier = Modifier.padding(padding)
        ) {
            composable("data") {
                ExperimentSensorsApp(sensorViewModel)
            }
            composable("allData"){
                AllDataScreen(sensorViewModel.getRecordedSamples())
            }
            composable("chart") {
                ChartScreen(sensorViewModel.getRecordedSamples())
            }
        }
    }
}