package bz.apps

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.ApplicationScope

interface Application {
    fun run(): @Composable ApplicationScope.() -> Unit
}