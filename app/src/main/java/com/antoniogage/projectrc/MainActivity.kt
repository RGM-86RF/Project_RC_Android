package com.antoniogage.projectrc

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.antoniogage.projectrc.ui.theme.ProjectRCTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        setContent {
            val dataStore = ThemeDataStore(this)
            val viewModel: ThemeViewModel by viewModels{
                object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return ThemeViewModel(dataStore) as T
                    }
                }

            }

            val theme by viewModel.theme.collectAsState()

            val useDarkTheme = when (theme) {
                "Light" -> false
                "Dark" -> true
                else -> isSystemInDarkTheme()
            }

            ProjectRCTheme(darkTheme = useDarkTheme) {
                val bleViewModel: BLEViewModel = viewModel()
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
                                    bleViewModel = bleViewModel,
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
                            ControllerScreen(bleViewModel = bleViewModel, onHomeClick = { navController.popBackStack()})
                        }
                    }
                }
            }
        }
    }
}
