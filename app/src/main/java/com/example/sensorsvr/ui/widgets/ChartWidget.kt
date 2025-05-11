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
    val username by dataViewModel.username

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
            return@Scaffold
        }

        val accelData = data.filter { it.sensorType == "accelerometer" } //todo gyros

        AndroidView(
            factory = { context ->
                LineChart(context).apply {
                    val entriesX = ArrayList<Entry>()
                    val entriesY = ArrayList<Entry>()
                    val entriesZ = ArrayList<Entry>()

                    accelData.forEachIndexed { index, sample ->
                        entriesX.add(Entry(index.toFloat(), sample.x))
                        entriesY.add(Entry(index.toFloat(), sample.y))
                        entriesZ.add(Entry(index.toFloat(), sample.z))
                    }

                    val dataSetX = LineDataSet(entriesX, "X").apply {
                        color = Color.RED
                        setDrawCircles(false)
                        lineWidth = 2f
                    }
                    val dataSetY = LineDataSet(entriesY, "Y").apply {
                        color = Color.GREEN
                        setDrawCircles(false)
                        lineWidth = 2f
                    }
                    val dataSetZ = LineDataSet(entriesZ, "Z").apply {
                        color = Color.BLUE
                        setDrawCircles(false)
                        lineWidth = 2f
                    }

                    this.data = LineData(dataSetX, dataSetY, dataSetZ)

                    axisRight.isEnabled = false
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.setDrawGridLines(false)
                    axisLeft.setDrawGridLines(true)

                    legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
                    legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER

                    description.text = "Accelerometer podatki"
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
