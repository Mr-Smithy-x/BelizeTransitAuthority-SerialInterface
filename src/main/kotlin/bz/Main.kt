package bz

import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import bz.apps.belimazon.Belimazon
import java.io.IOException
import kotlin.properties.Delegates

lateinit var state: WindowState

var TESTING by Delegates.notNull<Boolean>()

@Throws(IOException::class, InterruptedException::class)
fun main() {
    Config.load(".env.properties")
    TESTING = Config.getBoolean("TESTING")
    application(exitProcessOnExit = true, Belimazon.run())
}

