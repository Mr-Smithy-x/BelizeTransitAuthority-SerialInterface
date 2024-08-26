package bz.busfare.rw

import java.io.IOException

@Throws(IOException::class, InterruptedException::class)
fun main() {
    Config.load(".env.properties")
    val reader = UnoMifareReader("COM4")
    reader.open()
    reader.read()
}
