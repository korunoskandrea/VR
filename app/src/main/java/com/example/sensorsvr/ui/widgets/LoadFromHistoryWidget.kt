package com.example.sensorsvr.ui.widgets

import android.R.attr.data
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sensorsvr.model.SensorData
import com.example.sensorsvr.ui.navigation.BottomNavigationBar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

@Composable
fun LoadFromHistoryWidget(navController: NavController,
                          username: String) {
    val context = LocalContext.current
    var loadedData by remember { mutableStateOf<List<SensorData>>(emptyList()) }
    var loadedUsername by remember { mutableStateOf("Anonymous") }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            uri?.let {
                val name = getFileNameFromUri(context, it)
                loadedUsername = extractUsernameFromFileName(name)
                loadedData = readJsonFromUri(context, it)

                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set("loadedData", loadedData)

                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set("loadedUsername", loadedUsername)
            }
        }
    )

    Scaffold(
        bottomBar = {
            if (loadedData.isNotEmpty()) {
                BottomNavigationBar(navController, username, baseRoute = "history")
            }
        }

    ) { paddingValues ->
        Column(modifier = Modifier
            .padding(paddingValues)
            .padding(16.dp))
        {
            Text("Recorded data", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                filePickerLauncher.launch(arrayOf("application/json"))
            }) {
                Text("Choose JSON file")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (loadedData.isNotEmpty()) {
                Text("Recorded data from: {$username}", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(modifier = Modifier.fillMaxHeight()) {
                    items(loadedData.size) { index ->
                        val sample = loadedData[index]
                        Text("${sample.sensorType}: X=${sample.x}, Y=${sample.y}, Z=${sample.z} ob ${sample.timestamp}")
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

private fun readJsonFromUri(context: Context, uri: Uri): List<SensorData> {
    return try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val json = inputStream.bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<SensorData>>() {}.type
            Gson().fromJson(json, type) ?: emptyList()
        } ?: emptyList()
    } catch (e: IOException) {
        e.printStackTrace()
        emptyList()
    }
}

fun getFileNameFromUri(context: Context, uri: Uri): String {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    val nameIndex = cursor?.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
    cursor?.moveToFirst()
    val name = nameIndex?.let { cursor.getString(it) } ?: "unknown"
    cursor?.close()
    return name
}

fun extractUsernameFromFileName(fileName: String): String {
    val parts = fileName.removeSuffix(".json").split("_")
    return if (parts.size >= 2) parts[1] else "Anonymous"
}
