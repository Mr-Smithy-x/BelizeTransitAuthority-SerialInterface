package bz.apps.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import bz.Config
import bz.apps.ComposeApp
import bz.state
import bz.tracker.UDPTracker
import bz.tracker.usecase.impl.GPSUseCaseImpl
import bz.ui.const.Theme

object SampleApp : ComposeApp {

    override fun run(): @Composable ApplicationScope.() -> Unit = {
        Config.load(".env.properties")
        state = rememberWindowState(
            placement = WindowPlacement.Floating,
            isMinimized = false,
            position = WindowPosition(Alignment.Center),
            size = DpSize(300.dp, 300.dp)
        )

        var job by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }
        Window(
            onCloseRequest = {
                exitApplication()
            },
            resizable = false, undecorated = false,
            title = "GPS Test",
            state = state
        ) {
            MenuBar {
                Menu("Connection") {
                    Item("Start GPS Tracker") {
                        if(job?.isActive == true) {
                            job?.cancel()
                        }
                        job = UDPTracker.start(Config.getString("GPS_UDP_HOST")!!, Config.getInt("GPS_UDP_PORT")!!)
                    }
                    Item("Stop GPS Tracker") {
                        if(job != null) {
                            job?.cancel()
                        }
                        UDPTracker.reader.close()
                    }
                    Item("Exit") {
                        exitApplication()
                    }
                }
            }
            MaterialTheme(colors = darkColors(background = Theme.Base.background)) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp)
                ) {
                    val state by remember { UDPTracker.state }

                    @Composable
                    fun display(serial: GPSUseCaseImpl.GPSSerialState.Updated) {
                        val (latitude, longitude, speed, course, courseCardinal, satellites, hdop, altitude, datetime, age, charactersProcessed, sentencesFixed, failedCheckSum) = serial

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text("Sats: $satellites, HDOP: $hdop")
                            Text("Coords: $latitude, $longitude")
                            Text("Altitude: $altitude")
                            Text("Speed: $speed")
                            Text("Course: $course, $courseCardinal")
                            Text("Date Time: $datetime")
                            Text("Age: $age")
                        }
                    }

                    when(val serial = state) {
                        is GPSUseCaseImpl.GPSSerialState.Error -> Text(serial.exception.message?:"")
                        is GPSUseCaseImpl.GPSSerialState.Message -> Text(serial.message)
                        GPSUseCaseImpl.GPSSerialState.NoPosition -> Text("No position...")
                        is GPSUseCaseImpl.GPSSerialState.Success -> {
                            Text("Fixing Position...")
                        }
                        is GPSUseCaseImpl.GPSSerialState.Updated -> display(serial)

                    }
                }
            }
        }
    }

}