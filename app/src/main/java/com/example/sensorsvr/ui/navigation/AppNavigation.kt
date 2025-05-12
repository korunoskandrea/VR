package com.example.sensorsvr.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sensorsvr.ui.widgets.AllDataWidget
import com.example.sensorsvr.ui.widgets.AnalysisWidget
import com.example.sensorsvr.ui.widgets.ChartWidget
import com.example.sensorsvr.ui.widgets.HomeWidget
import com.example.sensorsvr.ui.widgets.RecordDataWidget
import com.example.sensorsvr.viewModel.DataViewModel
import com.example.sensorsvr.viewModel.SensorViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val sensorViewModel: SensorViewModel = viewModel()
    val dataViewModel: DataViewModel = viewModel()

    Scaffold(
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") {
                HomeWidget(
                    onRecordClick = { navController.navigate("record") },
                    onLoadClick = { navController.navigate("loadHistory") }
                )
            }

            composable("loadHistory") {
                dataViewModel.setIsHistory(true)
                AllDataWidget(navController = navController, dataViewModel)
            }

            composable("analysis") {
                AnalysisWidget(navController = navController, dataViewModel)
            }

            composable("allData") {
                AllDataWidget(navController = navController, dataViewModel)
            }


            composable("chart") {
                ChartWidget(navController = navController, dataViewModel)
            }

            composable("record") {
                RecordDataWidget(
                    navController = navController,
                    sensorViewModel = sensorViewModel,
                    dataViewModel
                )
            }

        }
    }
}