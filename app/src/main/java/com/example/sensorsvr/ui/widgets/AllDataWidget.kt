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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sensorsvr.R
import com.example.sensorsvr.model.IconRouteTabItem
import com.example.sensorsvr.model.SensorData
import com.example.sensorsvr.ui.navigation.BottomNavigationBar

@Composable
fun AllDataWidget(
    navController: NavController,
    username: String,
    data: List<SensorData>
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                tabs = listOf(
                    IconRouteTabItem(name = "record", route = "record", icon = R.drawable.screen_record_24dp_000000_fill0_wght400_grad0_opsz24),
                    IconRouteTabItem(name = "Analysis", route = "analysis/$username", icon = R.drawable.query_stats_24dp_000000_fill0_wght400_grad0_opsz24),
                    IconRouteTabItem(name = "All Data", route = "allData/$username", icon = R.drawable.menu_24dp_000000_fill0_wght400_grad0_opsz24),
                    IconRouteTabItem(name = "Graph", route = "chart/$username", icon = R.drawable.bar_chart_24dp_000000_fill0_wght400_grad0_opsz24),
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Recent recorded data", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(data) { sample ->
                    Text("(${sample.sensorType}) X: ${sample.x}, Y: ${sample.y}, Z: ${sample.z} Cas ${sample.timestamp}")
                    HorizontalDivider()
                }
            }
        }
    }
}