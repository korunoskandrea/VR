package com.example.sensorsvr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.sensorsvr.ui.navigation.AppNavigation
import com.example.sensorsvr.ui.theme.SensorsVRTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SensorsVRTheme {
               // ExperimentSensorsApp()
                AppNavigation()
            }
        }
    }
}

