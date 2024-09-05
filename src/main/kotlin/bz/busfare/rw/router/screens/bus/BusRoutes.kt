package bz.busfare.rw.router.screens.bus

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bz.busfare.rw.BZFare
import bz.busfare.rw.Stats
import bz.busfare.rw.models.Location
import bz.busfare.rw.router.Path
import bz.busfare.rw.router.Route
import bz.busfare.rw.state
import bz.busfare.rw.router.ui.SelectionButton
import bz.busfare.rw.router.ui.const.Theme

val routes = arrayOf(
    Pair(Location.BELIZE_CITY, Location.BELMOPAN),
    Pair(Location.BELMOPAN, Location.BELIZE_CITY),


    Pair(Location.BELMOPAN, Location.COROZAL),
    Pair(Location.COROZAL, Location.BELMOPAN),

    Pair(Location.BELIZE_CITY, Location.COROZAL),
    Pair(Location.COROZAL, Location.BELIZE_CITY),

    )


@Composable
fun BusRouteScreen() {
    Column {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize()
        ) {
            items(routes) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    val modifier = Modifier.fillMaxSize()
                        .heightIn(max = state.size.height / 2 - 32.dp, min = state.size.height / 2 - 32.dp)
                        .widthIn(max = state.size.width / 2 - 32.dp, min = state.size.width / 2 - 32.dp)
                        .padding(16.dp).fillMaxSize()
                    SelectionButton(
                        "${it.first.javaClass.simpleName} to ${it.second.javaClass.simpleName}",
                        modifier,
                        colors = Theme.Button
                    ) {
                        Stats.currentRoute.value = it
                        Route.path.value = Path.HOME
                    }
                }
            }
        }

    }
}

@Preview
@Composable
fun PreviewBusRoutes() {
    BusRouteScreen()
}

