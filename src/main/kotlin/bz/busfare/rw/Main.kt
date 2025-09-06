package bz.busfare.rw


import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import bz.busfare.rw.BZFare.openAndRead
import bz.busfare.rw.BZFare.reader
import bz.busfare.rw.router.Path
import bz.busfare.rw.router.Route
import bz.busfare.rw.router.screens.bus.BusRouteScreen
import bz.busfare.rw.router.screens.diagnostics.DiagnosticScreen
import bz.busfare.rw.router.screens.home.HomeScreen
import bz.busfare.rw.router.screens.settings.SettingScreen
import bz.busfare.rw.router.screens.statistics.StatisticsScreen
import bz.busfare.rw.router.ui.const.Theme
import java.io.IOException

lateinit var state: WindowState

@Throws(IOException::class, InterruptedException::class)
fun main() = application {
    Config.load(".env.properties")
    state = rememberWindowState(
        placement = WindowPlacement.Fullscreen,
        isMinimized = false,
        WindowPosition(Alignment.Center)
    )
    var isReaderOpened by remember { mutableStateOf(reader.isOpened) }

    Window(
        onCloseRequest = {
            if (reader.isOpened) {
                isReaderOpened = !reader.close()
            }
            exitApplication()
        },
        resizable = false, undecorated = true,
        title = "Belize Transportation Authority",
        state = state
    ) {
        MenuBar {
            Menu("File") {
                if (isReaderOpened) {
                    Item("Restart") {
                        openAndRead {
                            isReaderOpened = it
                        }
                    }
                    Item("Stop") {

                    }
                } else {
                    Item("Open") {
                        openAndRead {
                            isReaderOpened = it
                        }
                    }
                }
                Item("Bypass Screen") {
                    isReaderOpened = true
                }
                Item("Exit") {
                    exitApplication()
                }
            }
        }
        MaterialTheme(colors = darkColors(background = Theme.Base.background)) {
            if (isReaderOpened) {
                when (Route.path.value) {
                    Path.DIAGNOSTIC -> DiagnosticScreen()
                    Path.HOME -> HomeScreen()
                    Path.ROUTES -> BusRouteScreen()
                    Path.SETTINGS -> SettingScreen()
                    Path.STATS -> StatisticsScreen()
                }
            } else {
                Button(
                    onClick = {
                        openAndRead {
                            isReaderOpened = it
                        }
                    }, shape = RectangleShape,
                    modifier = Modifier.fillMaxSize(),
                    colors = Theme.Button
                ) {
                    Text("Start")
                }
            }
        }
    }
}

