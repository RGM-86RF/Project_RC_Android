package com.antoniogage.projectrc


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import androidx.compose.ui.graphics.Color
import com.antoniogage.projectrc.ui.theme.backgroundNightPurp



@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.M)
@Preview(showBackground = true)
@Composable
fun ConnectionScreen(
    bleViewModel: BLEViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onDeviceConnected:() -> Unit = {}){

    val context = LocalContext.current
    val connectionStatus by bleViewModel.connectionStatus.collectAsState()

    var showDialog by remember { mutableStateOf(true) }

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
            showDialog = false
            onDeviceConnected()
        }
    }


    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
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


        if(showDialog){
            AlertDialog(
                containerColor = backgroundNightPurp,
                onDismissRequest = { showDialog = false },
                title = {
                    Text(
                        text = "Connect Device",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                        },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ){
                        when{
                            !permissionState.allPermissionsGranted -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(vertical = 20.dp)
                                ) {
                                    Text("Permissions are required to scan for devices.")
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(onClick = { permissionState.launchMultiplePermissionRequest() }) {
                                        Text("Grant permissions")
                                    }
                                }
                            }
                            connectionStatus == ConnectionStatus.CONNECTING || connectionStatus == ConnectionStatus.CONNECTED -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(vertical = 40.dp)
                                ) {
                                    CircularProgressIndicator()
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text("Connecting...")
                                }
                            }

                            else -> {
                                FindDevicesScreen { clickedDevice ->
                                    bleViewModel.connect(context, clickedDevice)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {showDialog = false}) {
                        Text(
                            text = "Close",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Black
                            )
                    }
                }
            )

        }

//        Box(modifier = Modifier.weight(1f)){
//            when {
//                !permissionState.allPermissionsGranted -> {
//                    Column(
//                        modifier = Modifier.fillMaxSize(),
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        verticalArrangement = Arrangement.Center
//                    ) {
//                        Text("Please grant location permissions")
//                        Button(onClick = { permissionState.launchMultiplePermissionRequest() }) {
//                            Text("Grant permissions")
//                        }
//                    }
//                }
//
//
//                 connectionStatus == ConnectionStatus.CONNECTING || connectionStatus == ConnectionStatus.CONNECTED -> {
//                    Column(
//                        modifier = Modifier.fillMaxSize(),
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        verticalArrangement = Arrangement.Center
//                    ) {
//                        CircularProgressIndicator()
//                        Text("Connecting...")
//                    }
//                }
//                else -> {
//                    FindDevicesScreen { clickedDevice ->
//                        bleViewModel.connect(context, clickedDevice)
//                    }
//                }
//            }
//        }

    }

}


