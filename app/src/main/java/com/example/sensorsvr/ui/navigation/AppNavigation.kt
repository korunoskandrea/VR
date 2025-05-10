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
import com.example.sensorsvr.ui.screen.AllDataScreen
import com.example.sensorsvr.ui.screen.AnalysisScreen
import com.example.sensorsvr.ui.screen.ChartScreen
import com.example.sensorsvr.ui.screen.ExperimentSensorsApp
import com.example.sensorsvr.ui.screen.HomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val sensorViewModel: SensorViewModel = viewModel()

    Scaffold(
//        bottomBar = {
//            BottomNavigationBar(navController)
//        }
    ) { paddingValues ->
        NavHost(navController = navController, startDestination = "home", modifier = Modifier.padding(paddingValues)) {
            composable("home") {
                HomeScreen(
                    onRecordClick = { navController.navigate("data") },
                    onLoadClick = { navController.navigate("loadHistory") } // placeholder za kasneje
                )
            }
            composable("data") {
                ExperimentSensorsApp(
                    sensorViewModel = sensorViewModel,
                    onShowAllData = { navController.navigate("allData") },
                    onShowChart = { navController.navigate("chart") },
                    onAnalyze = { username, _ ->
                        navController.navigate("analysis/${username}")
                    }
                )
            }

            composable("allData") {
                AllDataScreen(sensorViewModel.getRecordedSamples())
            }

            composable("chart") {
                ChartScreen(sensorViewModel.getRecordedSamples())
            }

            composable("analysis/{username}") { backStackEntry ->
                val username = backStackEntry.arguments?.getString("username") ?: "neznano"
                AnalysisScreen(username, sensorViewModel.getRecordedSamples())
            }
        }
    }
}