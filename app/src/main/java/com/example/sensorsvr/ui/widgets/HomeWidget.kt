package com.example.sensorsvr.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width

import androidx.compose.material3.Button
import com.example.sensorsvr.R

@Composable
fun HomeWidget(
    onRecordClick:() -> Unit,
    onLoadClick:() -> Unit
) {

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        Button(onClick = onRecordClick, modifier = Modifier.fillMaxWidth())
        {
            Icon(
                painter = painterResource(id = R.drawable.material_symbols_outlined_fiber_manual_record),
                contentDescription = "Record Button Icon",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Record data")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onLoadClick, modifier = Modifier.fillMaxWidth()) {
            Icon(
                painter = painterResource(id = R.drawable.material_symbols_outlined_history),
                contentDescription = "Record Button Icon",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Load data")
        }
    }
}