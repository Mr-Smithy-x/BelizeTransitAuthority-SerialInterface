package bz.apps

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.*
import bz.Config
import bz.state
import bz.ui.const.Theme

object SampleApp: Application {

    override fun run(): @Composable ApplicationScope.() -> Unit = {
        Config.load(".env.properties")
        state = rememberWindowState(
            placement = WindowPlacement.Fullscreen,
            isMinimized = false,
            WindowPosition(Alignment.Center)
        )

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

                    }) {
                        Text("Read GPS")
                    }
                }
            }
        }
    }

}