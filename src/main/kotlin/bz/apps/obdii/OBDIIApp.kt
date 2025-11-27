package bz.apps.obdii

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import bz.apps.ComposeApp
import bz.apps.obdii.viewmodel.OBDIIViewModel
import bz.state
import bz.ui.const.Theme

object OBDIIApp : ComposeApp {

    override fun run(): @Composable() (ApplicationScope.() -> Unit) = {
        state = rememberWindowState(
            placement = WindowPlacement.Floating,
            isMinimized = false,
            position = WindowPosition(Alignment.Center),
            size = DpSize(500.dp, 300.dp)
        )
        val vm by remember { mutableStateOf(OBDIIViewModel()) }

        Window(
            onCloseRequest = {
                exitApplication()
            },
            resizable = false, undecorated = false,
            title = "OBD Test",
            state = state
        ) {
            MenuBar {
                Menu("Connection") {
                    Item("Start OBDII") {
                        vm.start()
                    }
                    Item("Stop OBDII") {
                        vm.stop()
                    }
                    Item("Exit") {
                        exitApplication()
                    }
                }
            }
            MaterialTheme(colors = darkColors(background = Theme.Base.background)) {
                mainDisplay(vm = vm)
            }
        }
    }

    @Composable
    private fun mainDisplay(vm: OBDIIViewModel) {
        Column(Modifier.padding(16.dp).fillMaxSize()) {
            Button(onClick = {
                vm.test()
            }) {
                Text("Test RPM")
            }
        }
    }

}