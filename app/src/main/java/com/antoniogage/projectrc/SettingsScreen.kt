package com.antoniogage.projectrc

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Preview(showBackground = true)
@Composable
fun SettingsScreen(onHomeClick: () -> Unit = {}) {
    // Implement your settings screen UI here
    Box(Modifier.fillMaxSize().padding(24.dp)){
        IconButton({onHomeClick()},
            Modifier.align(Alignment.TopStart)){
                Icon(Icons.Default.Home,"Home")
            }
        Text(
            "Settings",
            Modifier.align(Alignment.TopCenter),
            style = MaterialTheme.typography.headlineSmall,
            )
        Column(
            Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Settings",
                style = MaterialTheme.typography.headlineLarge,

            )
        }
    }
}