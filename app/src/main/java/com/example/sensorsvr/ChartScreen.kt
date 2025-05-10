package com.example.sensorsvr

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import android.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet


@Composable
fun ChartScreen(data: List<SensorData>){
    AndroidView(factory = { context ->
        val chart = LineChart(context)

        val x = ArrayList<Entry>()
        val y = ArrayList<Entry>()
        val z = ArrayList<Entry>()

        data.forEachIndexed{ index, sample ->
            if (sample.sensorType == "accelerometer") { // todo gyroscope
                x.add(Entry(index.toFloat(), sample.x))
                y.add(Entry(index.toFloat(), sample.y))
                z.add(Entry(index.toFloat(), sample.z))
            }
        }
        val dataSetX = LineDataSet(x, "X").apply { color = Color.RED }
        val dataSetY = LineDataSet(y, "Y").apply { color = Color.GREEN }
        val dataSetZ = LineDataSet(z, "Z").apply { color = Color.BLUE }

        val lineData = LineData(dataSetX, dataSetY, dataSetZ)
        chart.data = lineData
        chart.description.text = "Accelerometer podatki"
        chart.legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP

        chart.invalidate()
        chart
    })
}