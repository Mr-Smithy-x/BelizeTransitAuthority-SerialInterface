package bz.busfare.rw.router.screens.home

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bz.busfare.rw.router.Route
import bz.busfare.rw.router.Route.mainRoutes
import bz.busfare.rw.state
import bz.busfare.rw.router.ui.SelectionButton
import bz.busfare.rw.router.ui.const.Theme


@Composable
@Preview
fun HomeScreen() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize()
    ) {
        items(mainRoutes.size) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                val modifier = Modifier.fillMaxSize()
                    .heightIn(max = state.size.height / 2 - 32.dp, min = state.size.height / 2 - 32.dp)
                    .widthIn(max = state.size.width / 2 - 32.dp, min = state.size.width / 2 - 32.dp)
                    .padding(16.dp).fillMaxSize()
                SelectionButton(
                    mainRoutes[it].name,
                    modifier,
                    colors = Theme.Button
                ) {
                    Route.path.value = mainRoutes[it]
                }
            }
        }
    }
}