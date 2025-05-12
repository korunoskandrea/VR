package com.example.sensorsvr.ui.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sensorsvr.ui.navigation.BottomNavigationBar
import com.example.sensorsvr.ui.navigation.TopNavBar
import com.example.sensorsvr.utils.getBottomNavigationTabs
import com.example.sensorsvr.viewModel.DataViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AllDataWidget(
    navController: NavController,
    dataViewModel: DataViewModel = viewModel()
) {
    val isHistory by dataViewModel.isHistory
    val allData by dataViewModel.data

    val gyroData = allData.filter { it.sensorType == "gyroscope" }
    val accelData = allData.filter { it.sensorType == "accelerometer" }
    val maxCount = maxOf(gyroData.size, accelData.size)

    val dateFormatter = remember { SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopNavBar(navController = navController, dataViewModel)
        },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                tabs = getBottomNavigationTabs(isHistory)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth()) {
                Text(
                    "Gyroscope",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    "Accelerometer",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(maxCount) { index ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        val gyro = gyroData.getOrNull(index)
                        val accel = accelData.getOrNull(index)

                        Column(modifier = Modifier.weight(1f)) {
                            gyro?.let {
                                Text("X: ${"%.5f".format(it.x)}")
                                Text("Y: ${"%.5f".format(it.y)}")
                                Text("Z: ${"%.5f".format(it.z)}")
                                Text("Time: ${dateFormatter.format(Date(it.timestamp))}")
                            }
                            HorizontalDivider()
                        }
                        VerticalDivider()
                        Column(modifier = Modifier.weight(1f)) {
                            accel?.let {
                                Text("X: ${"%.5f".format(it.x)}")
                                Text("Y: ${"%.5f".format(it.y)}")
                                Text("Z: ${"%.5f".format(it.z)}")
                                Text("Time: ${dateFormatter.format(Date(it.timestamp))}")
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}
