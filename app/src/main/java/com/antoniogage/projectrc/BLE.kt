package com.antoniogage.projectrc

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothProfile
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

val CHARACTERISTIC_UUID: UUID = UUID.fromString("8451f5a9-bc8b-419b-b075-3838072fda82")
val SERVICE_UUID: UUID = UUID.fromString("fca99450-0455-4e26-8023-7657ec9bb1eb")

enum class ConnectionStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    FAILED
}




@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun FindBLEDevices() {
    BluetoothBox {
        FindDevicesScreen {
            Log.d("FindDeviceSample", "Name: ${it.name} Address: ${it.address} Type: ${it.type}")
        }
    }
}

@SuppressLint("InlinedApi", "MissingPermission")
@RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
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
        // Get a list of previously paired devices
        mutableStateListOf<BluetoothDevice>(*adapter.bondedDevices.toTypedArray())
    }


    // This effect will start scanning for devices when the screen is visible
    // If scanning is stop removing the effect will stop the scanning.

        LaunchedEffect(scanning) {
        if (scanning) {
            val leScanCallback: ScanCallback = object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult) {
                    if(!devices.any { it.address == result.device.address }) {
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
            Text(text = "Available devices", style = MaterialTheme.typography.titleSmall)
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
            modifier = Modifier.padding(16.dp),
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
                    Text(text = "Saved devices",
                        style = MaterialTheme.typography.titleSmall)
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
        Text(bluetoothDevice.address)
        val state = when (bluetoothDevice.bondState) {
            BluetoothDevice.BOND_BONDED -> "Paired"
            BluetoothDevice.BOND_BONDING -> "Pairing"
            else -> "None"
        }
        Text(text = state)

    }
}


@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun ConnectGATTSample() {
    var selectedDevice by remember {
        mutableStateOf<BluetoothDevice?>(null)
    }
    var connectionStatus by remember {
        mutableStateOf(ConnectionStatus.DISCONNECTED)
    }

    // Check that BT permissions and that BT is available and enabled
    BluetoothBox {
        AnimatedContent(targetState = selectedDevice, label = "SelectedDevice") { device ->
            if (device == null) {
                // Scans for BT devices and handles clicks (see FindDeviceSample)
                FindDevicesScreen { clickedDevice ->
                    selectedDevice = clickedDevice
                }
            } else {
                // Once a device is selected show the UI and try to connect device
                BLEConnectEffect(device = device,
                    onConnectionStatusChange = { newStatus ->
                        connectionStatus = newStatus

                        if (newStatus == ConnectionStatus.DISCONNECTED || newStatus == ConnectionStatus.FAILED) {
                            selectedDevice = null
                        }
                    })

                Column(modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center){
                    when(connectionStatus){
                        ConnectionStatus.CONNECTING -> {
                            CircularProgressIndicator()
                            Text("Connecting to ${device.name ?: device.address}")
                        }
                        ConnectionStatus.CONNECTED -> {
                            Text("Connected")
                    }
                        else -> {
                        }
                    }
                }

            }
        }
    }
}



@SuppressLint("MissingPermission")
private fun sendData(
    gatt: BluetoothGatt,
    characteristic: BluetoothGattCharacteristic,
) {
    val data = "Hello world!".toByteArray()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        gatt.writeCharacteristic(
            characteristic,
            data,
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT,
        )
    } else {
        // TODO: Replace with non-deprecated version
        characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        @Suppress("DEPRECATION")
        characteristic.value = data
        @Suppress("DEPRECATION")
        gatt.writeCharacteristic(characteristic)
    }
}


@SuppressLint("InlinedApi", "MissingPermission")
@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
@Composable
fun BLEConnectEffect(
    device: BluetoothDevice,
    onConnectionStatusChange: (ConnectionStatus) -> Unit,
) {
    val context = LocalContext.current
    val currentOnConnectionStatusChange by rememberUpdatedState(onConnectionStatusChange)

//TODO: Move to ViewModel to have connection shared with ControllerScreen
    DisposableEffect(device.address) {
        val gattCallback = object : BluetoothGattCallback() {
            override fun onConnectionStateChange(
                gatt: BluetoothGatt,
                status: Int,
                newState: Int,
            ) {
                if (status == BluetoothGatt.GATT_SUCCESS){
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        Log.i("BLEConnectEffect", "Connected to ${device.address}")
                        currentOnConnectionStatusChange(ConnectionStatus.CONNECTED)
                        gatt.discoverServices()
                    }else if (newState == BluetoothProfile.STATE_DISCONNECTED){
                        Log.i("BLEConnectEffect", "Disconnected from ${device.address}")
                        currentOnConnectionStatusChange(ConnectionStatus.DISCONNECTED)
                    }
                }
                else {
                    Log.w("BLEConnectEffect", "Error $status encountered for ${device.address}, Disconnecting")
                    currentOnConnectionStatusChange(ConnectionStatus.FAILED)
                }
            }


            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
               if(status == BluetoothGatt.GATT_SUCCESS){
                   Log.i("BLEConnectEffect", "Discovered services for ${device.address}")

               }else{
                   Log.w("BLEConnectEffect", "Discovery failed with status $status for ${device.address}")
               }
            }


        }



      Log.d("BLEConnectEffect", "Connecting to ${device.name}...")
        currentOnConnectionStatusChange(ConnectionStatus.CONNECTING)
        val gatt = device.connectGatt(context, false, gattCallback)



        onDispose {
            Log.d("BLEConnectEffect", "onDispose")
            gatt.disconnect()
            gatt.close()
        }
    }
}



@Preview(showBackground = true)
@Composable
fun FindBLEDevicesPreview() {
    FindBLEDevices()
}


