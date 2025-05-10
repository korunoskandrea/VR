package com.example.sensorsvr.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar (navController: NavController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == "data",
            onClick = { navController.navigate("data") },
            label = { Text("Podatki") },
            icon = { Icon(Icons.Default.List, contentDescription = null) }
        )
        NavigationBarItem(
            selected = currentRoute == "allData",
            onClick = { navController.navigate("allData") },
            label = { Text("Vsi Podatki") },
            icon = { Icon(Icons.Default.AccountBox, contentDescription = null) }
        )
        NavigationBarItem(
            selected = currentRoute == "chart",
            onClick = { navController.navigate("chart") },
            label = { Text("Graf") },
            icon = { Icon(Icons.Outlined.Info, contentDescription = null) }
        )
    }
}