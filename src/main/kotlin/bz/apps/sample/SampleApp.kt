package bz.apps.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.*
import bz.Config
import bz.apps.ComposeApp
import bz.state
import bz.tracker.UDPTracker
import bz.ui.const.Theme

object SampleApp : ComposeApp {

    override fun run(): @Composable ApplicationScope.() -> Unit = {
        Config.load(".env.properties")
        state = rememberWindowState(
            placement = WindowPlacement.Fullscreen,
            isMinimized = false,
            WindowPosition(Alignment.Center)
        )

        var job by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }
        Window(
            onCloseRequest = {
                exitApplication()
            },
            resizable = false, undecorated = true,
            title = "Belize Transportation Authority",
            state = state
        ) {
            MenuBar {
                Menu("File") {
                    Item("Exit") {
                        exitApplication()
                    }
                }
            }
            MaterialTheme(colors = darkColors(background = Theme.Base.background)) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Button(onClick = {
                        if(job?.isActive == true) {
                            job?.cancel()
                        }
                        job = UDPTracker.start("127.0.0.1", 9000)
                    }) {
                        Text("Read GPS")
                    }
                    Button(onClick = {
                        if(job != null) {
                            job?.cancel()
                        }
                    }) {
                        Text("Stop")
                    }
                }
            }
        }
    }

}