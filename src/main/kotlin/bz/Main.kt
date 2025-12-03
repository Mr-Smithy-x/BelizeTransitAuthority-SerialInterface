package bz

import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import bz.apps.ComposeApp
import bz.apps.belimazon.Belimazon
import bz.apps.busfare.BTAApplication
import bz.apps.obdii.OBDIIApp
import bz.apps.rokuapp.RokuApp
import bz.apps.tracker.TrackerApp
import com.fazecast.jSerialComm.SerialPort
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.properties.Delegates

lateinit var state: WindowState

var TESTING by Delegates.notNull<Boolean>()
var DEBUG by Delegates.notNull<Boolean>()

@Throws(IOException::class, InterruptedException::class)
fun main() {
    Config.load(getPortableEnvFile())
    TESTING = Config.getBoolean("TESTING")
    DEBUG = Config.getBoolean("DEBUG")
    if (DEBUG) {
        println("Testing mode: ${SerialPort.getCommPorts().map { it.systemPortName }}")
    }
    val content: ComposeApp = when (Config.getString("APP")) {
        "Roku" -> RokuApp
        "Belimazon" -> Belimazon
        "BTA" -> BTAApplication
        "Tracker" -> TrackerApp
        "OBD" -> OBDIIApp
        else -> RokuApp
    }

    val sppUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    application(exitProcessOnExit = true, content = content.run())
}

fun getPortableEnvFile(): File {
    val binaryPath = ProcessHandle.current().info().command().orElse(null) ?: return File("config.env.properties")
    val binaryFile = File(binaryPath)

    // Check if we are inside a macOS bundle
    // Path ends with: .../MyComposeApp.app/Contents/MacOS/MyComposeApp
    val isMacBundle = binaryPath.contains(".app/Contents/MacOS")

    val file = if (isMacBundle) {
        // macOS: Go up 3 levels to get out of the .app bundle
        // 1. Parent = MacOS
        // 2. Parent = Contents
        // 3. Parent = MyComposeApp.app
        // 4. Parent = The folder containing the app (e.g., /Applications)
        var currentDir = binaryFile.parentFile
        repeat(3) { currentDir = currentDir?.parentFile }
        File(currentDir, "config.env.properties")
    } else {
        // Windows/Linux: Just use the executable's folder
        File(binaryFile.parentFile, "config.env.properties")
    }

    if (file.exists()) return file
    return File("config.env.properties")
}
