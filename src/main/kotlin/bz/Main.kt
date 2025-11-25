package bz

import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import bz.apps.ComposeApp
import bz.apps.belimazon.Belimazon
import bz.apps.busfare.BTAApplication
import bz.apps.rokuapp.RokuApp
import bz.apps.tracker.TrackerApp
import com.fazecast.jSerialComm.SerialPort
import java.io.IOException
import java.util.*
import kotlin.properties.Delegates

lateinit var state: WindowState

var TESTING by Delegates.notNull<Boolean>()

@Throws(IOException::class, InterruptedException::class)
fun main() {
    Config.load(".env.properties")
    TESTING = Config.getBoolean("TESTING")
    println("Testing mode: ${SerialPort.getCommPorts().map { it.systemPortName }}")

    val content: ComposeApp = when (Config.getString("APP")) {
        "Roku" -> RokuApp
        "Belimazon" -> Belimazon
        "BTA" -> BTAApplication
        "Tracker" -> TrackerApp
        else -> RokuApp
    }
    //
    val sppUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    application(exitProcessOnExit = true, content = content.run())
}

