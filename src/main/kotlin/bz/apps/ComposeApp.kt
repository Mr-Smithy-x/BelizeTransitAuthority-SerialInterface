package bz.apps

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.ApplicationScope

interface ComposeApp {
    fun run(): @Composable ApplicationScope.() -> Unit
}