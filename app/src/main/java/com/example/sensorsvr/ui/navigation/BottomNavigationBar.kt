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
    username: String,
    baseRoute: String = "record",
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        if (baseRoute == "record") {
            NavigationBarItem(
                selected = currentRoute == "record",
                onClick = {
                    navController.navigate("record") {
                        popUpTo("record") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.screen_record_24dp_000000_fill0_wght400_grad0_opsz24),
                        contentDescription = "Record",
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text("Record") }
            )
        }

        // Analyse
        NavigationBarItem(
            selected = currentRoute?.startsWith("$baseRoute/analysis") == true,
            onClick = {
                if (username.isNotBlank()) {
                    navController.navigate("analysis/${username}") {
                        launchSingleTop = true
                    }
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

        // All Data
        NavigationBarItem(
            selected = currentRoute == "$baseRoute/allData",
            onClick = {
                navController.navigate("$baseRoute/allData") {
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.menu_24dp_000000_fill0_wght400_grad0_opsz24),
                    contentDescription = "All Data",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Display All Data") }
        )

        // Charts
        NavigationBarItem(
            selected = currentRoute == "$baseRoute/chart",
            onClick = {
                navController.navigate("$baseRoute/chart") {
                    launchSingleTop = true
                }
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