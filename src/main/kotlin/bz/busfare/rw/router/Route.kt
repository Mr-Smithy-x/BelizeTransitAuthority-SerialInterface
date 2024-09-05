package bz.busfare.rw.router

import androidx.compose.runtime.mutableStateOf

object Route {

    val path = mutableStateOf<Path>(Path.HOME)


    val mainRoutes = arrayOf(
        Path.ROUTES,
        Path.STATS,
        Path.SETTINGS,
        Path.DIAGNOSTIC,
    ).toList()


}