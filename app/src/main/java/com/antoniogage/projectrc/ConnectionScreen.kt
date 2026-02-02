package com.antoniogage.projectrc

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState



@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.M)
@Preview(showBackground = true)
@Composable
fun ConnectionScreen(
    bleViewModel: BLEViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onDeviceConnected:() -> Unit = {}){

    val context = LocalContext.current
    val connectionStatus by bleViewModel._connectionStatus.collectAsState()

    val blePermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

        listOf(
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
        )
    } else {
        listOf(
            android.Manifest.permission.BLUETOOTH,
            android.Manifest.permission.BLUETOOTH_ADMIN)
    }
    val permissionState = rememberMultiplePermissionsState(permissions = blePermissions)

    LaunchedEffect(Unit){
        permissionState.launchMultiplePermissionRequest()
    }


    LaunchedEffect(connectionStatus){
        if(connectionStatus == ConnectionStatus.READY){
            onDeviceConnected()
        }
    }


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
        Box(modifier = Modifier.weight(1f)){
            when{
                !permissionState.allPermissionsGranted -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ){
                        Text("Please grant location permissions")
                        Button(onClick = { permissionState.launchMultiplePermissionRequest() }){
                            Text("Grant permissions")
                        }
                }
            }


            }
            if(connectionStatus == ConnectionStatus.CONNECTING || connectionStatus == ConnectionStatus.CONNECTED){
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){
                    CircularProgressIndicator()
                    Text("Connecting...")
                }
            }
            else{
                FindDevicesScreen { clickedDevice ->
                    bleViewModel.connect(context, clickedDevice)
                }
            }
        }

    }

}


