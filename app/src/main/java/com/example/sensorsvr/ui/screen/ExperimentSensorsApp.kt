package com.example.sensorsvr.ui.screen


import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.unit.dp
import com.example.sensorsvr.model.SensorData
import com.example.sensorsvr.viewModel.SensorViewModel
import com.example.sensorsvr.utils.saveToJson


@Composable
fun ExperimentSensorsApp(
    sensorViewModel: SensorViewModel = viewModel(),
    onShowAllData: () -> Unit = {},
    onShowChart: () -> Unit = {},
    onAnalyze: (String, List<SensorData>) -> Unit = { _, _ -> }
) {
    val data by sensorViewModel.data.collectAsState()
    val context = LocalContext.current
    var experimentName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var isCollecting by remember { mutableStateOf(false) }

    val lastAccel = data.lastOrNull { it.sensorType == "accelerometer" }
    val lastGyro = data.lastOrNull { it.sensorType == "gyroscope" }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text("Zajem eksperiementa", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = experimentName,
            onValueChange = { experimentName = it },
            label = {
                Text("Ime eksperimenta")
            }
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = {
                Text("Ime uporabnika")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Button(onClick = {
                sensorViewModel.startListening()
                Toast.makeText(context, "Recording has started", Toast.LENGTH_SHORT).show()
            }) {
                Text("Zacni zajem")
            }
            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                sensorViewModel.stopListening()
                Toast.makeText(context, "Recording stopped", Toast.LENGTH_SHORT).show()
            }) {
                Text("Stopiraj zajem")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                sensorViewModel.clearData()
                Toast.makeText(context, "Podatki izbrisani", Toast.LENGTH_SHORT).show()
            }) {
                Text("Počisti")
            }

        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            saveToJson(context, experimentName, sensorViewModel.getRecordedSamples())
        }) {
            Text("Save in JSON file")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            onAnalyze(username, sensorViewModel.getRecordedSamples())
        }) {
            Text("Analysis")
        }


        Spacer(modifier = Modifier.height(16.dp))

        Text("Status: ${if (isCollecting) "Zajem v teku..." else "Zajem ustavljen."}",
            style = MaterialTheme.typography.labelLarge)

        Spacer(modifier = Modifier.height(16.dp))

        lastAccel?.let {
            Text(" Pospeškometer:")
            Text("X: ${it.x}, Y: ${it.y}, Z: ${it.z}")
            Text("Čas: ${it.timestamp}")
        }

        Spacer(modifier = Modifier.height(8.dp))

        lastGyro?.let {
            Text("Žiroskop:")
            Text("X: ${it.x}, Y: ${it.y}, Z: ${it.z}")
            Text("Čas: ${it.timestamp}")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("All recorded data ",
            style = MaterialTheme.typography.titleMedium)

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(data) { sample ->
                Text("(${sample.sensorType}) X: ${sample.x}, Y: ${sample.y}, Z: ${sample.z} @ ${sample.timestamp}")
                HorizontalDivider()
            }
        }
    }
}