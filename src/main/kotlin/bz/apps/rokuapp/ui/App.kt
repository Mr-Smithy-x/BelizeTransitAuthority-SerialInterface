package bz.apps.rokuapp.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import bz.apps.rokuapp.RokuApp
import bz.apps.rokuapp.models.Device
import bz.apps.rokuapp.utils.Roku

@Composable
@Preview
fun App(device: Device) {
    MaterialTheme {
        when (device) {
            Device.None -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(onClick = {
                        RokuApp.discover()
                    }) {
                        Text("Discover")
                    }
                }
            }

            is Device.RokuDevice -> {
                val state by remember { Roku.appState }
                RokuDeviceMain(state)
            }
        }
    }
}