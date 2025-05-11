package com.example.sensorsvr.utils

import com.example.sensorsvr.R
import com.example.sensorsvr.model.IconRouteTabItem

fun getBottomNavigationTabs(isHistoryWidget: Boolean): List<IconRouteTabItem> {
    val list = mutableListOf(
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
        )
    )

    if (!isHistoryWidget) {
        list.add(
            IconRouteTabItem(
                name = "record",
                route = "record",
                icon = R.drawable.screen_record_24dp_000000_fill0_wght400_grad0_opsz24
            ),
        )
    }

    return list
}