package com.example.sensorsvr.ui.widgets

import android.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sensorsvr.model.SensorData
import com.example.sensorsvr.ui.navigation.BottomNavigationBar
import com.example.sensorsvr.ui.navigation.TopNavBar
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
    val chartRef = remember { mutableStateOf<LineChart?>(null) }

    val accelData = data.filter { it.sensorType == "accelerometer" }
    val gyroData = data.filter { it.sensorType == "gyroscope" }

    Scaffold(
        topBar = { TopNavBar(navController = navController, dataViewModel) },
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
        ) {
            if (data.isEmpty()) {
                EmptyStateMessage()
            } else {
                SensorChart(
                    chartRef = chartRef,
                    accelData = accelData,
                    gyroData = gyroData,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyStateMessage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No Sensor Data Available",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Start recording to see live data visualization",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
    }
}

@Composable
private fun SensorChart(
    chartRef: MutableState<LineChart?>,
    accelData: List<SensorData>,
    gyroData: List<SensorData>,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(accelData, gyroData) {
        chartRef.value?.let { chart ->
            val lineData = createLineData(accelData, gyroData)
            chart.data = lineData
            configureChartAppearance(chart)
            chart.invalidate()
        }
    }

    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                chartRef.value = this
                setupBaseChartConfig()
            }
        },
        modifier = modifier
    )
}

private fun createLineData(
    accelData: List<SensorData>,
    gyroData: List<SensorData>
): LineData {
    return LineData(
        listOf(
            createDataSet(accelData.map { it.x }, "Accel X", Color.RED),
            createDataSet(accelData.map { it.y }, "Accel Y", Color.GREEN),
            createDataSet(accelData.map { it.z }, "Accel Z", Color.BLUE),
            createDataSet(gyroData.map { it.x }, "Gyro X", Color.MAGENTA),
            createDataSet(gyroData.map { it.y }, "Gyro Y", Color.CYAN),
            createDataSet(gyroData.map { it.z }, "Gyro Z", Color.YELLOW)
        )
    )
}

private fun createDataSet(
    values: List<Float>,
    label: String,
    color: Int
): LineDataSet {
    val entries = values.mapIndexed { index, value ->
        Entry(index.toFloat(), value)
    }
    return LineDataSet(entries, label).apply {
        setDrawCircles(false)
        lineWidth = 2f
        setColor(color)
        mode = LineDataSet.Mode.LINEAR
        setDrawValues(false)
    }
}

private fun configureChartAppearance(chart: LineChart) {
    chart.axisRight.isEnabled = false
    chart.xAxis.apply {
        position = XAxis.XAxisPosition.BOTTOM
        setDrawGridLines(false)
        textColor = Color.DKGRAY
    }
    chart.axisLeft.apply {
        setDrawGridLines(true)
        gridColor = Color.LTGRAY
        textColor = Color.DKGRAY
    }
    chart.legend.apply {
        verticalAlignment = Legend.LegendVerticalAlignment.TOP
        horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        textColor = Color.DKGRAY
    }
    chart.description.apply {
        text = "Sensor Data Visualization"
        textColor = Color.DKGRAY
    }
    chart.setBackgroundColor(Color.WHITE)
    chart.setDrawGridBackground(false)
}

private fun LineChart.setupBaseChartConfig() {
    description.text = "Loading sensor data..."
    setTouchEnabled(true)
    isDragEnabled = true
    setScaleEnabled(true)
    setPinchZoom(true)
    setDrawGridBackground(false)
    setBackgroundColor(Color.WHITE)
}