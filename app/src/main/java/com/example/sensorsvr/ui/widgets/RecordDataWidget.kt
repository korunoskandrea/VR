package com.example.sensorsvr.ui.widgets

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sensorsvr.R
import com.example.sensorsvr.ui.navigation.BottomNavigationBar
import com.example.sensorsvr.utils.getBottomNavigationTabs
import com.example.sensorsvr.utils.saveToJson
import com.example.sensorsvr.viewModel.DataViewModel
import com.example.sensorsvr.viewModel.SensorViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RecordDataWidget(
    navController: NavController,
    sensorViewModel: SensorViewModel = viewModel(),
    dataViewModel: DataViewModel = viewModel()
) {
    dataViewModel.setIsHistory(false)

    val context = LocalContext.current
    val data by sensorViewModel.data.collectAsState()

    var recordingName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var sampleFrequency by remember { mutableStateOf("10") }
    var isRecording by remember { mutableStateOf(false) }

    val dateFormatter = remember { SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()) }

    Scaffold(
        bottomBar = {
            if (data.isNotEmpty()) {
                BottomNavigationBar(
                    navController = navController,
                    tabs = getBottomNavigationTabs(false)
                )
            }
        }

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Record data", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = recordingName,
                onValueChange = { recordingName = it },
                label = { Text("Recording name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            )

            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it;
                    dataViewModel.setUsername(username)
                },
                label = { Text("Username") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp, 0.dp)
            )

            OutlinedTextField(
                value = sampleFrequency,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() }) {
                        sampleFrequency = newValue
                    }
                },
                label = { Text("Sampling frequency (Hz)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(
                    onClick = {
                        val hz = sampleFrequency.toIntOrNull() ?: 10
                        val delayMicros = 1_000_000 / hz
                        sensorViewModel.startListening(delayMicros)
                        isRecording = true
                        Toast.makeText(context, "Recording started", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(72.dp)
                ) {
                    Icon(
                        Icons.Filled.PlayArrow,
                        contentDescription = "Start",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(
                    onClick = {
                        sensorViewModel.stopListening()
                        isRecording = false
                        Toast.makeText(context, "Recording stopped", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(72.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.stop_24dp_000000_fill0_wght400_grad0_opsz24),
                        contentDescription = "Stop",
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(
                    onClick = {
                        saveToJson(
                            context,
                            recordingName,
                            username,
                            sensorViewModel.getRecordedSamples()
                        )
                        Toast.makeText(context, "Data is saved", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(72.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.download_24dp_000000_fill0_wght400_grad0_opsz24),
                        contentDescription = "Save",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            if (!isRecording && data.isNotEmpty()) {
                dataViewModel.setData(data)
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(data) { sample ->
                        val formattedTime = dateFormatter.format(Date(sample.timestamp))
                        Text("${sample.sensorType} -> X: ${sample.x}, Y: ${sample.y}, Z: ${sample.z} time: ${formattedTime}")
                        HorizontalDivider()
                    }
                }
            }

        }
    }
}
