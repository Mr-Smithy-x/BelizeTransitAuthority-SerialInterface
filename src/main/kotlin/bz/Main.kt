package bz

import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import bz.apps.belimazon.Belimazon
import java.io.IOException

lateinit var state: WindowState


@Throws(IOException::class, InterruptedException::class)
fun main() {
    application(exitProcessOnExit = true, Belimazon.run())
}

