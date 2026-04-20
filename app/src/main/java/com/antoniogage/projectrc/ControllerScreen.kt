package com.antoniogage.projectrc

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.antoniogage.projectrc.ui.theme.PurpleGrey40
import com.antoniogage.projectrc.ui.theme.lightPurp
import com.antoniogage.projectrc.ui.theme.orangeish
import com.antoniogage.projectrc.ui.theme.redishPurple


@Composable
fun ControllerScreen(
    bleViewModel: BLEViewModel,
    onHomeClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

    Box(
        modifier = Modifier.fillMaxSize()
            .padding(
                horizontal = 75.dp,
                vertical = 24.dp
            ),
        contentAlignment = Alignment.TopStart
    ) {
        FilledTonalIconButton(onClick = {
            bleViewModel.disconnect()
            onHomeClick()
        }) {
            Icon(Icons.Default.Home, "Home", tint = lightPurp)

        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
            .padding(
                horizontal = 75.dp,
                vertical = 24.dp
            ),
        contentAlignment = Alignment.TopEnd
    ) {
        FilledTonalIconButton(onClick = {
            onSettingsClick()
        }) {
            Icon(Icons.Default.Settings,"Settings", tint = lightPurp)

        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
            .padding(
                horizontal = 75.dp,
                vertical = 24.dp
            ),
        contentAlignment = Alignment.BottomStart
    ) {
        DpadControl(bleViewModel = bleViewModel)
    }
    Box(
        modifier = Modifier.fillMaxSize()
            .padding(
                horizontal = 75.dp,
                vertical = 24.dp
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            FreeRoamButton(bleViewModel = bleViewModel)
            SpeedSlider(bleViewModel = bleViewModel)
        }
    }
    Box(
        modifier = Modifier.fillMaxSize()
            .padding(
                horizontal = 35.dp,
                vertical = 75.dp
            ),
        contentAlignment = Alignment.BottomEnd
    ) {
        RightDpadControl(bleViewModel = bleViewModel)
    }



}

@Composable
fun DpadControl(bleViewModel: BLEViewModel){
    val dpadsize = 85.dp
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        HoldButton(
            modifier = Modifier.size(dpadsize),
            unselectedImage = R.drawable.dpad_up,
            selectedImage = R.drawable.filled_up,
            onClick = {bleViewModel.motorWrite(Commands.forward) },
            contentDescription = "Forward",
            onRelease = {bleViewModel.motorWrite(Commands.stop) }
        )
//        Row(modifier = Modifier.fillMaxWidth(0.25f),
//            horizontalArrangement = Arrangement.SpaceBetween)
//        {
//
//            HoldButton(
//                unselectedImage = R.drawable.dpad_left,
//                selectedImage = R.drawable.filled_left,
//                onClick = {bleViewModel.motorWrite(Commands.left) },
//                contentDescription = "left",
//                onRelease = {bleViewModel.motorWrite(Commands.stop)}
//            )
//
//            HoldButton(
//                unselectedImage = R.drawable.dpad_right,
//                selectedImage = R.drawable.filled_right,
//                onClick = {bleViewModel.motorWrite(Commands.right) },
//                contentDescription = "Right",
//                onRelease = {bleViewModel.motorWrite(Commands.stop)}
//            )
//        }

        HoldButton(
            modifier = Modifier.size(dpadsize),
            unselectedImage = R.drawable.dpad_down,
            selectedImage = R.drawable.filled_down,
            onClick = {bleViewModel.motorWrite(Commands.backward) },
            contentDescription = "Down",
            onRelease = {bleViewModel.motorWrite(Commands.stop)}
        )

    }
}

@Composable
fun RightDpadControl(bleViewModel: BLEViewModel){
    val dpadsize = 85.dp
    Row(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically)
    {

        HoldButton(
            modifier = Modifier.size(dpadsize),
            unselectedImage = R.drawable.dpad_left,
            selectedImage = R.drawable.filled_left,
            onClick = {bleViewModel.motorWrite(Commands.left) },
            contentDescription = "left",
            onRelease = {bleViewModel.motorWrite(Commands.stop)}
        )

        HoldButton(
            modifier = Modifier.size(dpadsize),
            unselectedImage = R.drawable.dpad_right,
            selectedImage = R.drawable.filled_right,
            onClick = {bleViewModel.motorWrite(Commands.right) },
            contentDescription = "Right",
            onRelease = {bleViewModel.motorWrite(Commands.stop)}
        )
    }
}

@Composable
private fun LockScreenOrientation(@Suppress("SameParameterValue") orientation: Int) {
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


@Composable
fun HoldButton(
    unselectedImage: Int,
    selectedImage: Int,
    contentDescription: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onRelease: () -> Unit
){
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val releasedListener by rememberUpdatedState(onRelease)
    val pressedListener by rememberUpdatedState(onClick)
    val interactions = remember { mutableStateListOf<Interaction>() }


    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    interactions.add(interaction)
                    pressedListener()

                }
                is PressInteraction.Release -> {
                    interactions.remove(interaction.press)
                    releasedListener()
                }
                is PressInteraction.Cancel -> {
                    interactions.remove(interaction.press)
                    releasedListener()
                }

            }
        }

    }


    IconButton(
        modifier = modifier,
        onClick = {  },
        interactionSource = interactionSource
    ) {
        Icon(
            painter = if (isPressed) painterResource(id = selectedImage) else painterResource( id = unselectedImage),
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            tint = orangeish
        )
    }
}


@Composable
fun SpeedSlider(bleViewModel: BLEViewModel)
{
    val currentSpeed by bleViewModel.motorSpeed.collectAsState()
    val displaySpeed = if(currentSpeed >= 150) {
        ((currentSpeed - 150).toFloat() / (255-150)*100).toInt()
    } else{
        0
    }
    Column(
        modifier = Modifier.fillMaxWidth(0.35f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ){
        Slider(
            value = currentSpeed.toFloat(),
            onValueChange = { newPosition ->
                bleViewModel.updateMotorSpeed(newPosition.toInt())
            },
            onValueChangeFinished = {
                // move onValueChange lambda to here.
                val speedValue = currentSpeed.toString()
                bleViewModel.motorWrite(speedValue.toByteArray())
            },
            valueRange = 150f..255f,
            colors = androidx.compose.material3.SliderDefaults.colors(
                thumbColor = orangeish,
                activeTrackColor = orangeish,
                inactiveTrackColor = PurpleGrey40,

            )
        )
            Text(text = displaySpeed.toString())

    }

}

@Composable
fun FreeRoamButton(bleViewModel: BLEViewModel) {
    var isToggled by remember { mutableStateOf(false) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        FilledTonalIconButton(onClick = {
            isToggled = !isToggled
            if (isToggled) bleViewModel.motorWrite(Commands.freeRoamOn)
            else bleViewModel.motorWrite(Commands.freeRoamOff)
        }) {
            Icon(
                painter = if (isToggled) painterResource(id = R.drawable.free_roam_on)
                else painterResource(id = R.drawable.free_roam_off),
                contentDescription = "Free Roam",
                tint = orangeish,
                //modifier = Modifier.offset(x = (-3).dp, y = 10.dp)
            )
        }
        Text(
            text = "Free Roam",
            color = lightPurp,
            style = androidx.compose.material3.MaterialTheme.typography.labelSmall
        )

    }
}


@Preview(showBackground = true)
@Composable
fun ControllerScreenPreview() {
    ControllerScreen(bleViewModel = viewModel())
}


