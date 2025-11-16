package bz

import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import bz.apps.ComposeApp
import bz.apps.belimazon.Belimazon
import bz.apps.busfare.BTAApplication
import bz.apps.rokuapp.RokuApp
import java.io.IOException
import kotlin.properties.Delegates

lateinit var state: WindowState

var TESTING by Delegates.notNull<Boolean>()

@Throws(IOException::class, InterruptedException::class)
fun main() {
    Config.load(".env.properties")
    TESTING = Config.getBoolean("TESTING")

    val content: ComposeApp = when(Config.getString("APP")){
        "Roku" -> RokuApp
        "Belizamon" -> Belimazon
        "BTA" -> BTAApplication
        else -> RokuApp
    }
    application(exitProcessOnExit = true, content.run())
}

