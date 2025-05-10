package com.example.sensorsvr.ui.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sensorsvr.model.SensorData

@Composable
fun AllDataWidget (data: List<SensorData>) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("📋 Vsi zbrani podatki", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(data) { sample ->
                Text("(${sample.sensorType}) X: ${sample.x}, Y: ${sample.y}, Z: ${sample.z} Cas ${sample.timestamp}")
                HorizontalDivider()
            }
        }
    }
}