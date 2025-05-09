package com.example.sensorsvr


import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.unit.dp


@Composable
fun ExperimentSensorsApp(sensorViewModel: SensorViewModel = viewModel()){
    val data by sensorViewModel.data.collectAsState()
    val context = LocalContext.current
    var experimentName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxSize()){
        Text("Zajem eksperiementa", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = experimentName,
            onValueChange = { experimentName = it },
            label = {
                Text("Ime eksperimenta")
            }
        )
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = {
                Text("Opis eksperimenta")
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
        }

        Button(onClick = {
            sensorViewModel.stopListening()
            Toast.makeText(context, "Recording stopped", Toast.LENGTH_SHORT).show()
        }) {
            Text("Stopiraj zajem")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            saveToCsv(context, experimentName, sensorViewModel.getRecordedSamples())
        }) {
            Text("Save in CSV file")
        }
        Spacer(modifier = Modifier.height(16.dp))

        data.lastOrNull()?.let { sample ->
            Text("Zadnji vzorec:", style = MaterialTheme.typography.titleMedium)
            Text("Senzor: ${sample.sensorType}")
            Text("X: ${sample.x}, Y: ${sample.y}, Z: ${sample.z}")
            Text("Čas: ${sample.timestamp}")
        }
    }


}