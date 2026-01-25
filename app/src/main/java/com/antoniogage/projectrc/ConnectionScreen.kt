package com.antoniogage.projectrc

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.os.Build
import androidx.annotation.RequiresApi
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


// TODO: Separating data from the UI boxes for Available and paired devices

@Preview(showBackground = true)
@Composable
fun ConnectionScreen(onBackClick: () -> Unit = {},
                     onDeviceConnected:() -> Unit = {}){
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ){
            IconButton({ onBackClick() })
            {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
        }
        }

        ConnectGatt(onDeviceConnected = onDeviceConnected)
    }

}


@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.M)
@Composable
private fun ConnectGatt(onDeviceConnected: () -> Unit) {
    var selectedDevice by remember { mutableStateOf<BluetoothDevice?>(null) }
    var connectionStatus by remember { mutableStateOf(ConnectionStatus.DISCONNECTED) }

    LaunchedEffect(connectionStatus) {
        if (connectionStatus == ConnectionStatus.CONNECTED) {
            onDeviceConnected()
        }

    }

    BluetoothBox {
        AnimatedContent(targetState = selectedDevice, label = "SelectedDevice") { device ->
            if (device == null) {
                FindDevicesScreen { clickedDevice ->
                    selectedDevice = clickedDevice
                }
            } else {
                BLEConnectEffect(device = device,
                    onConnectionStatusChange = { newStatus ->
                        connectionStatus = newStatus

                        if (newStatus == ConnectionStatus.DISCONNECTED || newStatus == ConnectionStatus.FAILED) {
                            selectedDevice = null
                        }
                    }
                )

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    when (connectionStatus) {
                        ConnectionStatus.CONNECTING -> {
                            CircularProgressIndicator()
                            Text("Connecting to ${device.name ?: device.address}")
                        }
                        ConnectionStatus.CONNECTED -> {
                            Text("Connected")
                        }
                        else -> {}
                    }
                }
            }

        }
    }
}