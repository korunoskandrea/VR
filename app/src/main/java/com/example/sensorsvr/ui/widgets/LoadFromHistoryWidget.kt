package com.example.sensorsvr.ui.widgets

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sensorsvr.R
import com.example.sensorsvr.model.IconRouteTabItem
import com.example.sensorsvr.ui.navigation.BottomNavigationBar
import com.example.sensorsvr.viewModel.DataViewModel

@Composable
fun LoadFromHistoryWidget(
    navController: NavController,
    dataViewModel: DataViewModel = viewModel()
) {
    dataViewModel.setIsHistory(true)
    val context = LocalContext.current
    val username by dataViewModel.username
    val data by dataViewModel.data

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            uri?.let {
                dataViewModel.loadFromFile(context, it)
            }
        }
    )

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                tabs = listOf(
                    IconRouteTabItem(
                        name = "Analysis",
                        route = "analysis",
                        icon = R.drawable.query_stats_24dp_000000_fill0_wght400_grad0_opsz24
                    ),
                    IconRouteTabItem(
                        name = "All Data",
                        route = "allData",
                        icon = R.drawable.menu_24dp_000000_fill0_wght400_grad0_opsz24
                    ),
                    IconRouteTabItem(
                        name = "Graph",
                        route = "chart",
                        icon = R.drawable.bar_chart_24dp_000000_fill0_wght400_grad0_opsz24
                    ),
                )
            )

        }

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        )
        {
            Text("Recorded data", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                filePickerLauncher.launch(arrayOf("application/json"))
            }) {
                Text("Choose JSON file")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (data.isNotEmpty()) {
                Text(
                    "Recorded data from: {$username}",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(modifier = Modifier.fillMaxHeight()) {
                    items(data.size) { index ->
                        val sample = data[index]
                        Text("${sample.sensorType}: X=${sample.x}, Y=${sample.y}, Z=${sample.z} ob ${sample.timestamp}")
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}