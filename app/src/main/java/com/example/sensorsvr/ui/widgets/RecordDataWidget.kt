package com.example.sensorsvr.ui.widgets

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sensorsvr.R
import com.example.sensorsvr.model.SensorData
import com.example.sensorsvr.mqtt.MqttClientManager
import com.example.sensorsvr.ui.navigation.BottomNavigationBar
import com.example.sensorsvr.ui.navigation.TopNavBar
import com.example.sensorsvr.utils.getBottomNavigationTabs
import com.example.sensorsvr.utils.saveToJson
import com.example.sensorsvr.viewModel.DataViewModel
import com.example.sensorsvr.viewModel.SensorViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.json.JSONObject
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

    val gyroData = data.filter { it.sensorType == "gyroscope" }
    val accelData = data.filter { it.sensorType == "accelerometer" }
    val maxCount = maxOf(gyroData.size, accelData.size)

    var recordingName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var sampleFrequency by remember { mutableStateOf("10") }
    var isRecording by remember { mutableStateOf(false) }

    val dateFormatter = remember { SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()) }

    var selectedMovement by remember { mutableStateOf("Straight") }
    var expanded by remember { mutableStateOf(false) }
    val movementOptions = listOf("Straight", "Up", "Down")

    val coroutineScope = rememberCoroutineScope()
    var publishJob by remember { mutableStateOf<Job?>(null) }
    val delayMillis = remember(sampleFrequency) {
        (1000 / (sampleFrequency.toIntOrNull() ?: 10).coerceAtLeast(1))
    }


    Scaffold(
        topBar = { TopNavBar(navController = navController) },
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
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = recordingName,
                    onValueChange = { recordingName = it },
                    label = { Text("Recording Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        dataViewModel.setUsername(username)
                    },
                    label = { Text("Username") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = sampleFrequency,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() }) {
                            sampleFrequency = newValue
                        }
                    },
                    label = { Text("Sampling Frequency (Hz)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedMovement,
                        onValueChange = { },
                        label = { Text("Movement Type") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(
                                onClick = { expanded = !expanded },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.arrow_drop_down_24dp_000000_fill0_wght400_grad0_opsz24),
                                    contentDescription = "Show options",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        movementOptions.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = option,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                },
                                onClick = {
                                    selectedMovement = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ControlButton(
                    iconRes = R.drawable.play_arrow_24dp_000000_fill0_wght400_grad0_opsz24,
                    contentDescription = "Start",
                    onClick = {
                        coroutineScope.launch {
                            try {
                                MqttClientManager.connectSuspend()

                                val hz = sampleFrequency.toIntOrNull() ?: 10
                                val delayMillis = 1000L / hz
                                sensorViewModel.startListening(delayMillis.toInt())
                                isRecording = true

                                publishJob = launch {
                                    while (isActive) {
                                        val latestAccel =
                                            sensorViewModel.getLatestSample("accelerometer")
                                        latestAccel?.let {
                                            val json = JSONObject().apply {
                                                put("x", it.x)
                                                put("y", it.y)
                                                put("z", it.z)
                                                put("timestamp", it.timestamp)
                                                put("movement", selectedMovement)
                                                put("user", username)
                                            }
                                            MqttClientManager.publish(
                                                "sensors/accel",
                                                json.toString()
                                            )
                                        }
                                        delay(delayMillis)
                                    }
                                }

                                Toast.makeText(context, "Recording started", Toast.LENGTH_SHORT)
                                    .show()

                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "MQTT povezava ni uspela: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }

                )

                ControlButton(
                    iconRes = R.drawable.stop_24dp_000000_fill0_wght400_grad0_opsz24,
                    contentDescription = "Stop",
                    onClick = {
                        sensorViewModel.stopListening()
                        isRecording = false
                        publishJob?.cancel()
                        publishJob = null
                        Toast.makeText(context, "Recording stopped", Toast.LENGTH_SHORT).show()
                    }
                )

                ControlButton(
                    iconRes = R.drawable.download_24dp_000000_fill0_wght400_grad0_opsz24,
                    contentDescription = "Save",
                    onClick = {
                        dataViewModel.setTrueLabel(selectedMovement)
                        saveToJson(
                            context,
                            recordingName,
                            username,
                            selectedMovement,
                            sensorViewModel.getRecordedSamples()
                        )
                        Toast.makeText(context, "Data is saved", Toast.LENGTH_SHORT).show()
                    }
                )

                ControlButton(
                    iconRes = R.drawable.close_24dp_000000_fill0_wght400_grad0_opsz24,
                    contentDescription = "Clear",
                    onClick = {
                        sensorViewModel.clearData()
                        isRecording = false
                        publishJob?.cancel()
                        publishJob = null
                        MqttClientManager.disconnect()
                        Toast.makeText(context, "Recording cleared", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            if (!isRecording && data.isNotEmpty()) {
                dataViewModel.setData(data)
                SensorDataDisplay(
                    gyroData = gyroData,
                    accelData = accelData,
                    maxCount = maxCount,
                    dateFormatter = dateFormatter
                )
            }
        }
    }
}

@Composable
private fun ControlButton(
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(64.dp)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun SensorDataDisplay(
    gyroData: List<SensorData>,
    accelData: List<SensorData>,
    maxCount: Int,
    dateFormatter: SimpleDateFormat
) {
    Column(modifier = Modifier.padding(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                "Gyroscope",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "Accelerometer",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.primary
            )
        }

        LazyColumn {
            itemsIndexed(List(maxCount) { it }) { index, _ ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    val gyro = gyroData.getOrNull(index)
                    val accel = accelData.getOrNull(index)

                    SensorDataColumn(
                        data = gyro,
                        dateFormatter = dateFormatter,
                        modifier = Modifier.weight(1f)
                    )

                    VerticalDivider(thickness = 1.dp)

                    SensorDataColumn(
                        data = accel,
                        dateFormatter = dateFormatter,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (index < maxCount - 1) {
                    HorizontalDivider(thickness = 1.dp)
                }
            }
        }
    }
}

@Composable
private fun SensorDataColumn(
    data: SensorData?,
    dateFormatter: SimpleDateFormat,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 8.dp)) {
        data?.let {
            Text("X: ${"%.5f".format(it.x)}")
            Text("Y: ${"%.5f".format(it.y)}")
            Text("Z: ${"%.5f".format(it.z)}")
            Text("Time: ${dateFormatter.format(Date(it.timestamp))}")
        }
    }
}
