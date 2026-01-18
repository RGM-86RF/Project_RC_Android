package com.antoniogage.projectrc


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.font.FontWeight


@Preview(showBackground = true)
@Composable
fun HomeScreen(
    onSettingsClick: () -> Unit = {},
    onConnectClick: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp))
    {
        IconButton({onSettingsClick()},
           Modifier.align(Alignment.TopEnd)
        )
    {   Icon(Icons.Default.Settings,"Settings")
    }
        Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally)
        {
            Text(
           text =  "Project RC",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        }
        Button(
            {onConnectClick()},
            Modifier.align(Alignment.BottomCenter).fillMaxWidth(0.8f)
        )
        {
        Text("Connect")
        }
    }
}
