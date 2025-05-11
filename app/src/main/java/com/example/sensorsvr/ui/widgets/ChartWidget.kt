package com.example.sensorsvr.ui.widgets

import android.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sensorsvr.ui.navigation.BottomNavigationBar
import com.example.sensorsvr.utils.getBottomNavigationTabs
import com.example.sensorsvr.viewModel.DataViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

@Composable
fun ChartWidget(
    navController: NavController,
    dataViewModel: DataViewModel = viewModel()
) {
    val isHistory by dataViewModel.isHistory
    val data by dataViewModel.data

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
            return@Scaffold
        }

        val accelData = data.filter { it.sensorType == "accelerometer" }
        val gyroData = data.filter { it.sensorType == "gyroscope" }

        AndroidView(
            factory = { context ->
                LineChart(context).apply {
                    val accelX = ArrayList<Entry>()
                    val accelY = ArrayList<Entry>()
                    val accelZ = ArrayList<Entry>()
                    val gyroX = ArrayList<Entry>()
                    val gyroY = ArrayList<Entry>()
                    val gyroZ = ArrayList<Entry>()

                    accelData.forEachIndexed { index, sample ->
                        accelX.add(Entry(index.toFloat(), sample.x))
                        accelY.add(Entry(index.toFloat(), sample.y))
                        accelZ.add(Entry(index.toFloat(), sample.z))
                    }

                    gyroData.forEachIndexed { index, sample ->
                        gyroX.add(Entry(index.toFloat(), sample.x))
                        gyroY.add(Entry(index.toFloat(), sample.y))
                        gyroZ.add(Entry(index.toFloat(), sample.z))
                    }

                    val dataSets = listOf(
                        LineDataSet(accelX, "Accel X").apply {
                            color = Color.RED
                            setDrawCircles(false)
                        },
                        LineDataSet(accelY, "Accel Y").apply {
                            color = Color.GREEN
                            setDrawCircles(false)
                        },
                        LineDataSet(accelZ, "Accel Z").apply {
                            color = Color.BLUE
                            setDrawCircles(false)
                        },
                        LineDataSet(gyroX, "Gyro X").apply {
                            color = Color.MAGENTA
                            setDrawCircles(false)
                        },
                        LineDataSet(gyroY, "Gyro Y").apply {
                            color = Color.CYAN
                            setDrawCircles(false)
                        },
                        LineDataSet(gyroZ, "Gyro Z").apply {
                            color = Color.YELLOW
                            setDrawCircles(false)
                        }
                    )

                    this.data = LineData(dataSets)

                    axisRight.isEnabled = false
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.setDrawGridLines(false)
                    axisLeft.setDrawGridLines(true)

                    legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
                    legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER

                    description.text = "Data from Accelerometer and Gyroscope"
                    setTouchEnabled(true)
                    isDragEnabled = true
                    setScaleEnabled(true)
                    invalidate()
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        )
    }
}
