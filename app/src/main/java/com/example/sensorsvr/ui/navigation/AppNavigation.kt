package com.example.sensorsvr.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.compose.ui.Modifier
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sensorsvr.model.SensorData
import com.example.sensorsvr.viewModel.SensorViewModel
import com.example.sensorsvr.ui.widgets.AllDataWidget
import com.example.sensorsvr.ui.widgets.AnalysisWidget
import com.example.sensorsvr.ui.widgets.ChartWidget
import com.example.sensorsvr.ui.widgets.HomeWidget
import com.example.sensorsvr.ui.widgets.LoadFromHistoryWidget
import com.example.sensorsvr.ui.widgets.RecordDataWidget
import com.google.gson.Gson

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

            composable("loadHistory"){
                LoadFromHistoryWidget(navController = navController, username = "Anonymous")
            }

            composable("history/analysis") {
                val gson = remember { Gson() }
                val json = navController.previousBackStackEntry?.savedStateHandle?.get<String>("loadedDataJson")
                val username = navController.previousBackStackEntry?.savedStateHandle?.get<String>("loadedUsername") ?: "Unknown"

                val type = object : com.google.gson.reflect.TypeToken<List<SensorData>>() {}.type
                val data = remember(json) { gson.fromJson<List<SensorData>>(json, type) ?: emptyList() }

                AnalysisWidget(navController = navController, username = username, data = data)
            }

            composable("history/allData") {
                val gson = remember { Gson() }
                val json = navController.previousBackStackEntry?.savedStateHandle?.get<String>("loadedDataJson")
                val username = navController.previousBackStackEntry?.savedStateHandle?.get<String>("loadedUsername") ?: "Unknown"

                val type = object : com.google.gson.reflect.TypeToken<List<SensorData>>() {}.type
                val data = remember(json) { gson.fromJson<List<SensorData>>(json, type) ?: emptyList() }

                AllDataWidget(navController = navController, username = username, data = data)
            }


            composable("history/chart") {
                val gson = remember { Gson() }
                val json = navController.previousBackStackEntry?.savedStateHandle?.get<String>("loadedDataJson")
                val username = navController.previousBackStackEntry?.savedStateHandle?.get<String>("loadedUsername") ?: "Unknown"

                val type = object : com.google.gson.reflect.TypeToken<List<SensorData>>() {}.type
                val data = remember(json) { gson.fromJson<List<SensorData>>(json, type) ?: emptyList() }

                ChartWidget(navController = navController, username = username, data = data)
            }



            composable("record") {
                RecordDataWidget(
                    navController = navController,
                    sensorViewModel = sensorViewModel,
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