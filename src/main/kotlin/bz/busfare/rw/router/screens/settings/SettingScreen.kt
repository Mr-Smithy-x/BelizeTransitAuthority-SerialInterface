package bz.busfare.rw.router.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bz.busfare.rw.Config
import bz.busfare.rw.router.Path
import bz.busfare.rw.router.Route
import bz.busfare.rw.router.ui.const.Theme

@Composable
fun SettingScreen() {
    val dictionary = Config.getSafeDictionary()
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = {
            Config.save(dictionary)
            Route.path.value = Path.HOME
        }, modifier = Modifier.fillMaxWidth(), colors = Theme.Button) {
            Text("Save")
        }
        LazyColumn {
            items(dictionary.size) { index ->
                val item = dictionary[index]
                Column(Modifier.fillMaxWidth().padding(8.dp)) {
                    Text(item.key.toString())
                    TextField(value = dictionary[index].value.toString(), onValueChange = { o ->
                        dictionary[index] = mutableMapOf(item.key to (o as Any)).entries.first()
                    })
                }
            }
        }
    }

}
