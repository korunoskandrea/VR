package com.example.sensorsvr.ui.navigation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.sensorsvr.R
import com.example.sensorsvr.viewModel.DataViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavBar(navController: NavController, dataViewModel: DataViewModel = viewModel()) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val title = when (currentRoute) {
        "record" -> "Record data"
        "loadHistory" -> "History data"
        "analysis" -> "Analysis"
        "allData" -> "All Data"
        "chart" -> "Graph"
        else -> "Sensor App"
    }

    val showTopBar = currentRoute != "home"
    val context = LocalContext.current
    val isHistory by dataViewModel.isHistory

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            uri?.let {
                dataViewModel.loadFromFile(context, it)
            }
        }
    )

    if (showTopBar) {
        TopAppBar(
            title = { Text(text = title) },
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_back_ios_24dp_000000_fill0_wght400_grad0_opsz24),
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                if (isHistory) {
                    IconButton(onClick = {
                        filePickerLauncher.launch(arrayOf("application/json"))
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.upload_file_24dp_000000_fill0_wght400_grad0_opsz24),
                            contentDescription = "Upload file"
                        )
                    }
                }
                IconButton(onClick = {
                    navController.navigate("home")
                }) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Go to Record"
                    )
                }
            }
        )
    }
}