package com.example.sensorsvr.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.compose.ui.Modifier
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sensorsvr.viewModel.SensorViewModel
import com.example.sensorsvr.ui.widgets.AllDataWidget
import com.example.sensorsvr.ui.widgets.AnalysisWidget
import com.example.sensorsvr.ui.widgets.ChartWidget
import com.example.sensorsvr.ui.widgets.HomeWidget
import com.example.sensorsvr.ui.widgets.RecordDataWidget

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val sensorViewModel: SensorViewModel = viewModel()

    Scaffold(
    ) { paddingValues ->
        NavHost(navController = navController, startDestination = "home", modifier = Modifier.padding(paddingValues)) {
            composable("home") {
                HomeWidget(
                    onRecordClick = { navController.navigate("record") },
                    onLoadClick = { navController.navigate("loadHistory") } // placeholder za kasneje
                )
            }
            composable("record") {
                RecordDataWidget(
                    navController = navController,
                    sensorViewModel = sensorViewModel,
                    onNavigateToAnalysis = { username ->
                        navController.navigate("analysis/$username")
                    },
                    onNavigateToAllData = { navController.navigate("allData") },
                    onNavigateToChart = { navController.navigate("chart") }
                )
            }

            composable("allData") {
                AllDataWidget(
                    navController = navController,
                    username = "Anonymous",
                    data = sensorViewModel.getRecordedSamples()
                )
            }

            composable("chart") {
                ChartWidget(
                    navController = navController,
                    username = "Anonymous",
                    data = sensorViewModel.getRecordedSamples()
                )
            }

            composable("analysis/{username}") { backStackEntry ->
                val username = backStackEntry.arguments?.getString("username") ?: "neznano"
                AnalysisWidget(navController=navController, username = "Anonymous", data = sensorViewModel.getRecordedSamples())
            }
        }
    }
}