package com.example.sensorsvr.viewModel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.example.sensorsvr.model.SensorData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class DataViewModel(application: Application) : AndroidViewModel(application) {
    private var _data = mutableStateOf<List<SensorData>>(emptyList())
    val data: State<List<SensorData>> = _data

    private var _username = mutableStateOf("Anonymous")
    val username: State<String> = _username

    private var _isHistory = mutableStateOf(false)
    val isHistory: State<Boolean> = _isHistory

    fun loadFromFile(context: Context, uri: Uri) {
        val name = getFileNameFromUri(context, uri)
        _username.value = extractUsernameFromFileName(name)
        _data.value = readJsonFromUri(context, uri)
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

    private fun getFileNameFromUri(context: Context, uri: Uri): String {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        val nameIndex = cursor?.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
        cursor?.moveToFirst()
        val name = nameIndex?.let { cursor.getString(it) } ?: "unknown"
        cursor?.close()
        return name
    }

    private fun extractUsernameFromFileName(fileName: String): String {
        val parts = fileName.removeSuffix(".json").split("_")
        return if (parts.size >= 2) parts[1] else "Anonymous"
    }

    fun setIsHistory(isHistory: Boolean) {
        _isHistory.value = isHistory
    }

    fun setUsername(username: String) {
        _username.value = username
    }

    fun addData(data: SensorData) {
        val dataList: MutableList<SensorData> = _data.value.toMutableList()
        dataList.add(data)
        _data.value = dataList
    }

    fun setData(dataList: List<SensorData>) {
        _data.value = dataList
    }
}