package com.example.sensorsvr.viewModel

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.AndroidViewModel
import com.example.sensorsvr.model.SensorData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SensorViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {
    private val sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer  = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    private val _accelData = MutableStateFlow<List<SensorData>>(emptyList())
    val accelData: StateFlow<List<SensorData>> = _accelData

    private val _gyroData = MutableStateFlow<List<SensorData>>(emptyList())
    val gyroData: StateFlow<List<SensorData>> = _gyroData

    private val _data = MutableStateFlow<List<SensorData>>(emptyList())
    val data: StateFlow<List<SensorData>> = _data
    private val recordedData = mutableListOf<SensorData>()

    var lastUserName: String? = null
    var lastAnalysisData: List<SensorData> = emptyList()


    fun startListening(delayMicros: Int = 100_000) {
        sensorManager.registerListener(this, accelerometer, delayMicros)
        sensorManager.registerListener(this, gyroscope, delayMicros)
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    fun clearData(){
        recordedData.clear()
        _data.value = emptyList()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val sensorName = when(it.sensor.type){
                Sensor.TYPE_ACCELEROMETER -> "accelerometer"
                Sensor.TYPE_GYROSCOPE -> "gyroscope"
                else -> "unknown"
            }

            val data = SensorData(
                x = it.values[0],
                y = it.values[1],
                z = it.values[2],
                sensorType = sensorName,
                timestamp = System.currentTimeMillis()
            )

            recordedData.add(data)
            _data.value = recordedData.toList() // for UI
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun getRecordedSamples(): List<SensorData>{
        return recordedData.toList()
    }
}