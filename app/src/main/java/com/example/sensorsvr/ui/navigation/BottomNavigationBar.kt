package com.example.sensorsvr.ui.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.sensorsvr.R

@Composable
fun BottomNavigationBar(
    navController: NavController,
    username: String
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        NavigationBarItem(
            selected = currentRoute?.startsWith("record") == true,
            onClick = {
                navController.navigate("record")
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.screen_record_24dp_000000_fill0_wght400_grad0_opsz24),
                    contentDescription = "Record data",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Record") }
        )

        NavigationBarItem(
            selected = currentRoute?.startsWith("analysis") == true,
            onClick = {
                if (username.isNotBlank()) {
                    navController.navigate("analysis/$username")
                }
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.query_stats_24dp_000000_fill0_wght400_grad0_opsz24),
                    contentDescription = "Analysis",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Analyse") }
        )

        NavigationBarItem(
            selected = currentRoute == "allData",
            onClick = {
                navController.navigate("allData")
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.menu_24dp_000000_fill0_wght400_grad0_opsz24),
                    contentDescription = "All data",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Display All Data") }
        )

        NavigationBarItem(
            selected = currentRoute == "chart",
            onClick = {
                navController.navigate("chart")
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.bar_chart_24dp_000000_fill0_wght400_grad0_opsz24),
                    contentDescription = "Graphs",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Graphs") }
        )
    }
}
