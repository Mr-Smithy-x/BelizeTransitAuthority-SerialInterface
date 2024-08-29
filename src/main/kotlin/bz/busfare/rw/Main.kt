package bz.busfare.rw


import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import bz.busfare.rw.BZFare.openAndRead
import bz.busfare.rw.BZFare.reader
import java.io.IOException


val numbers = (0..8).toList()

@Composable
@Preview
fun App() {
    MaterialTheme {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize()
        ) {
            items(numbers.size) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .heightIn(min = 180.dp)
                            .padding(16.dp).fillMaxSize()
                    ) {
                        Text(text = "  $it")
                    }
                }
            }
        }
    }
}

@Throws(IOException::class, InterruptedException::class)
fun main() = application {
    Config.load(".env.properties")
    val state = rememberWindowState(
        placement = WindowPlacement.Floating,
        isMinimized = false,
        WindowPosition(Alignment.Center)
    )
    var isReaderOpened by remember { mutableStateOf(reader.isOpened) }

    Window(
        onCloseRequest = {
            if (reader.isOpened) {
                isReaderOpened = !reader.close()
            }
            exitApplication()
        },
        resizable = false,
        title = "Belize Transportation Authority",
        state = state
    ) {
        MenuBar {
            Menu("File") {
                if (isReaderOpened) {
                    Item("Restart") {
                        openAndRead {
                            isReaderOpened = it
                        }
                    }
                    Item("Stop") {

                    }
                } else {
                    Item("Open") {
                        openAndRead {
                            isReaderOpened = it
                        }
                    }
                }
            }
        }
        if (isReaderOpened) {
            App()
        } else {
            Button(
                onClick = {
                    openAndRead {
                        isReaderOpened = it
                    }
                }, shape = RectangleShape,
                modifier = Modifier.fillMaxSize()
            ) {
                Text("Start")
            }
        }
    }
}

