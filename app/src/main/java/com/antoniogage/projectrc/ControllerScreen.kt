package com.antoniogage.projectrc

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun ControllerScreen(
    bleViewModel: BLEViewModel,
    onHomeClick: () -> Unit = {}
) {
    LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)


    IconButton(onClick = {
        bleViewModel.disconnect()
        onHomeClick()
    }){
        Icon(Icons.Default.Home,"Home")

    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {


        DpadControl(bleViewModel = bleViewModel)
    }

}

@Composable
fun DpadControl(bleViewModel: BLEViewModel){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        IconButton(onClick = {bleViewModel.motorWrite(Commands.forward)  }){
            Icon(painter = painterResource(id = R.drawable.dpad_up),
                contentDescription = "Up"

            )
        }
        Row(modifier = Modifier.fillMaxWidth(0.4f),
            horizontalArrangement = Arrangement.SpaceBetween)
        {
            IconButton(onClick = {bleViewModel.motorWrite(Commands.left) }){
                Icon(painter = painterResource(id = R.drawable.dpad_left),
                    contentDescription = "Left"

                )
            }
            IconButton(onClick = { bleViewModel.motorWrite(Commands.right)}){
                Icon(painter = painterResource(id = R.drawable.dpad_right),
                    contentDescription = "Right"

                )
            }
        }
        IconButton(onClick = {bleViewModel.motorWrite(Commands.backward) }){
            Icon(painter = painterResource(id = R.drawable.dpad_down),
                contentDescription = "Down"

            )
        }

    }
}

@Composable
private fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current

    DisposableEffect(Unit) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            // restore original orientation when view disappears
            activity.requestedOrientation = originalOrientation
        }
    }
}

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Preview(showBackground = true)
@Composable
fun ControllerScreenPreview() {
    ControllerScreen(bleViewModel = viewModel())
}


