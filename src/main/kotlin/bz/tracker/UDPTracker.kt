package bz.tracker

import bz.Config
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Thread.sleep
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlin.coroutines.CoroutineContext

object UDPTracker : CoroutineScope {

    private val socket = DatagramSocket()
    private val uuid by lazy {
        Config.getString("UUID") ?: error("UUID not found")
    }

    private fun pingLocation(latitude: Double, longitude: Double): JsonObject {
        val message = JsonObject().apply {
            addProperty("type", "cmd")
            addProperty("command", "update")
            val element = JsonObject()
            element.addProperty("uuid", uuid)
            element.addProperty("latitude", latitude)
            element.addProperty("longitude", longitude)
            add("data", element)
        }
        return message
    }

    fun start(ip: String, port: Int) = launch {
        val inetAddress = InetAddress.getByName(ip)

        while (true) {
            val messageBytes = pingLocation(40.7128, -74.0060).toString().toByteArray()
            try {
                val datagramPacket = DatagramPacket(messageBytes, messageBytes.size, inetAddress, port)
                socket.send(datagramPacket)
                println("Packet sent.")
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            println("Waiting to send more...")
            sleep(5000)
        }
    }

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO
}