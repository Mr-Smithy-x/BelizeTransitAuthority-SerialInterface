package bz.apps.busfare

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.window.*
import bz.TESTING
import bz.apps.Application
import bz.apps.busfare.rw.BZFare.openAndRead
import bz.apps.busfare.rw.BZFare.reader
import bz.apps.busfare.rw.router.Path
import bz.apps.busfare.rw.router.Route
import bz.apps.busfare.rw.router.screens.bus.BusRouteScreen
import bz.apps.busfare.rw.router.screens.diagnostics.DiagnosticScreen
import bz.apps.busfare.rw.router.screens.home.HomeScreen
import bz.apps.busfare.rw.router.screens.settings.SettingScreen
import bz.apps.busfare.rw.router.screens.statistics.StatisticsScreen
import bz.state
import bz.ui.const.Theme

object BTAApplication: Application {

    val testing get() = TESTING

    override fun run(): @Composable ApplicationScope.() -> Unit = {
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

}