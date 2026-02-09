package com.antoniogage.projectrc

import android.R.attr.theme
import android.app.AlertDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel


@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onHomeClick: () -> Unit = {}) {

    var showThemeDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val themeViewModel: ThemeViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ThemeViewModel(ThemeDataStore(context)) as T

            }
        }

    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings")},
                navigationIcon = {
                    IconButton(onClick =  onHomeClick ) {
                        Icon(Icons.Default.Home, contentDescription = "Home")
                    }
                }
            )

        }

    ) { innerPadding ->
        if (showThemeDialog){
            themeDialog(
                onDismiss = { showThemeDialog = false },
                onSelected = { theme ->
                    themeViewModel.saveTheme(theme)
                    showThemeDialog = false
                }
            )

        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ){
            SettingsItem(
                icon = Icons.Default.Star,
                title = "Theme",
                subtitle = "Change the app's theme",
                onClick = {showThemeDialog = true}
            )

            SettingsItem(
                icon = Icons.Default.Info,
                title = "Logfiles",
                subtitle = "Read the log files created by the database",
                onClick = {}

            )


        }
    }



//    Box(Modifier.fillMaxSize().padding(24.dp)){
//        IconButton({onHomeClick()},
//            Modifier.align(Alignment.TopStart)){
//                Icon(Icons.Default.Home,"Home")
//            }
//        Text(
//            "Settings",
//            Modifier.align(Alignment.TopCenter),
//            style = MaterialTheme.typography.headlineSmall,
//            )
//        Column(
//            Modifier.align(Alignment.Center),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(
//                "Settings",
//                style = MaterialTheme.typography.headlineLarge,
//
//            )
//        }
//    }
}

@Composable
private fun themeDialog(
    onDismiss: () -> Unit = {},
    onSelected: (String) -> Unit
) {
    val themeOptions = listOf("Light", "Dark", "System default")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Theme") },
        text = {
            Column {
                themeOptions.forEach { theme ->
                    Text(
                        text = theme,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelected(theme) }
                            .padding(vertical = 12.dp)

                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }

        }


    )
}



@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick:() -> Unit

){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement =  Arrangement.spacedBy(16.dp)
    ){
        Icon(icon, contentDescription = null)
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall)
        }
        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
    HorizontalDivider()
    }
