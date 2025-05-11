package com.example.sensorsvr.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.sensorsvr.model.IconRouteTabItem

@Composable
fun BottomNavigationBar(
    navController: NavController,
    tabs: List<IconRouteTabItem>
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        tabs.forEachIndexed { index, tabItem ->
            NavigationBarItem(
                selected = currentRoute == tabItem.route,
                onClick = {
                    navController.navigate(tabItem.route) {
                        launchSingleTop = true
                    }
                },
                label = { Text(tabItem.name) },
                icon = {
                    Icon(
                        painter = painterResource(tabItem.icon),
                        contentDescription = tabItem.name
                    )
                }
            )
        }
    }
}