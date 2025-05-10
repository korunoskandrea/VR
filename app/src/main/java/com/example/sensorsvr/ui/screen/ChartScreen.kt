package com.example.sensorsvr.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import android.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sensorsvr.model.SensorData
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet


@Composable
fun ChartScreen(data: List<SensorData>) {
    if (data.isEmpty()) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {
            Text("Ni podatkov za prikaz.")
        }
        return
    }

    val accelData = data.filter { it.sensorType == "accelerometer" }

    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                val entriesX = ArrayList<Entry>()
                val entriesY = ArrayList<Entry>()
                val entriesZ = ArrayList<Entry>()

                // Prikaz po indeksih
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

                val lineData = LineData(dataSetX, dataSetY, dataSetZ)
                this.data = lineData

                // Osi
                axisRight.isEnabled = false
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                axisLeft.setDrawGridLines(true)

                // Legenda
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
            .padding(16.dp)
    )
}