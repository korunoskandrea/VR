package com.example.sensorsvr.utils

import android.content.Context
import android.os.Environment
import android.widget.Toast
import com.example.sensorsvr.model.SensorData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun saveToCsv(context: Context, experimentName: String, data: List<SensorData>) {
    try {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "${experimentName}_$timeStamp.csv"
        // val dir = File(context.getExternalFilesDir(null), "sensor_data")
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!dir.exists()) dir.mkdirs()

        val file = File(dir, fileName)
        val writer = FileWriter(file)

        writer.append("timestamp,sensorType,x,y,z\n")
        for (d in data) {
            writer.append("${d.timestamp},${d.sensorType},${d.x},${d.y},${d.z}\n")
        }

        writer.flush()
        writer.close()

        Toast.makeText(context, "Podatki shranjeni v: ${file.absolutePath}", Toast.LENGTH_LONG)
            .show()
    } catch (e: Exception) {
        Toast.makeText(context, "Napaka pri shranjevanju: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

fun saveToJson(
    context: Context,
    recordingName: String,
    username: String,
    movementType: String,
    data: List<SensorData>
) {
    try {
        val gson: Gson = GsonBuilder().setPrettyPrinting().create()
        val jsonString = gson.toJson(data)

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        // : name_user_movement_timestamp.json
        val fileName =
            "${sanitize(recordingName)}_${sanitize(username)}_${sanitize(movementType)}_$timeStamp.json"
        // val dir = File(context.getExternalFilesDir(null), "sensor_data")
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!dir.exists()) dir.mkdirs()

        val file = File(dir, fileName)
        file.writeText(jsonString)

        Toast.makeText(context, "JSON saved in: ${file.absolutePath}", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Error while saving JSON file: ${e.message}", Toast.LENGTH_LONG)
            .show()
    }
}

private fun sanitize(input: String): String {
    return input.replace("[^a-zA-Z0-9]".toRegex(), "_")
}