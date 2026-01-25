package com.antoniogage.projectrc

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.antoniogage.projectrc.ui.theme.ProjectRCTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProjectRCTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "Home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("Home") {
                            HomeScreen(
                                onSettingsClick = { navController.navigate("settings") },
                                onConnectClick = { navController.navigate("connection") }
                            )
                        }
                        composable("Settings") { SettingsScreen(onHomeClick = { navController.popBackStack() }) }
                        composable("Connection") {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                ConnectionScreen(
                                    onBackClick = { navController.popBackStack() },
                                    onDeviceConnected = {
                                        navController.navigate("controller") {
                                            popUpTo("connection") {
                                                inclusive = true
                                            }
                                        }
                                    }
                                )
                        }

                        composable("Controller") {
                            ControllerScreen()
                        }
                    }
                }
            }
        }
    }
}
