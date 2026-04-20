package com.antoniogage.projectrc

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay






@SuppressLint("InlinedApi", "MissingPermission")
@Composable
internal fun FindDevicesScreen(onConnect: (BluetoothDevice) -> Unit) {
    val context = LocalContext.current
    val adapter = checkNotNull(context.getSystemService(BluetoothManager::class.java).adapter)
    var scanning by remember {
        mutableStateOf(false)
    }
    val devices = remember {
        mutableStateListOf<BluetoothDevice>()
    }
    val pairedDevices = remember {
        mutableStateListOf<BluetoothDevice>(*adapter.bondedDevices.toTypedArray())
    }




        LaunchedEffect(scanning) {
        if (scanning) {
            val leScanCallback: ScanCallback = object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult) {
                    if(!devices.any { it.address == result.device.address } && result.device.name == "Project RC") {
                        devices.add(result.device)
                    }
                }
                override fun onScanFailed(errorCode: Int) {
                    Log.w("FindBLEDevicesSample", "BLE Scan Failed with code $errorCode")
                    scanning = false


                }
            }
            adapter.bluetoothLeScanner.startScan(leScanCallback)
            Log.d("FindBLEDevicesSample", "BLE Scan Started")


            delay(15000)
            if (scanning) {
                adapter.bluetoothLeScanner.stopScan(leScanCallback)
                scanning = false
                Log.d("FindBLEDevicesSample", "BLE Scan Stopped")
            }
        }
    }
        LaunchedEffect(Unit) {
            scanning = true
        }


    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(54.dp)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Available devices: ",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold)
            if (scanning) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            } else {
                IconButton(
                    onClick = {
                        devices.clear()
                        scanning = true
                    },
                ) {
                    Icon(imageVector = Icons.Rounded.Refresh, contentDescription = null)
                }
            }
        }

        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (devices.isEmpty() && !scanning) {
                item {
                    Text(text = "No devices found")
                }
            }
            items(devices, key = { it.address }) { item ->
                BluetoothDeviceItem(
                    bluetoothDevice = item,
                    onConnect = onConnect,
                )
            }

            if (pairedDevices.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    Text(text = "Saved devices: ",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold)
                }
                items(pairedDevices, key = { it.address }) {
                    BluetoothDeviceItem(
                        bluetoothDevice = it,
                        onConnect = onConnect,
                    )
                }
            }
        }
    }

}

@SuppressLint("MissingPermission")
@Composable
internal fun BluetoothDeviceItem(
    bluetoothDevice: BluetoothDevice,

    onConnect: (BluetoothDevice) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .clickable { onConnect(bluetoothDevice) },
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = bluetoothDevice.name ?: bluetoothDevice.address,
            style = TextStyle(fontWeight = FontWeight.Bold),
        )
//        Text(bluetoothDevice.address)
//        val state = when (bluetoothDevice.bondState) {
//            BluetoothDevice.BOND_BONDED -> "Paired"
//            BluetoothDevice.BOND_BONDING -> "Pairing"
//            else -> "None"
//        }
//        Text(text = state)
    }
    HorizontalDivider()
}








@Preview(showBackground = true)
@Composable
fun FindBLEDevicesPreview() {
    //FindBLEDevices()
}


