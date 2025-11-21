package bz.tracker

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import bz.Config
import bz.tracker.usecase.impl.GPSUseCaseImpl
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
    private val _state: MutableState<GPSUseCaseImpl.GPSSerialState> = mutableStateOf(GPSUseCaseImpl.GPSSerialState.NoPosition)
    private val state: State<GPSUseCaseImpl.GPSSerialState> get() = _state

    val reader by lazy {
        val string = Config.getString("PORT")
        GPSReader(string!!)
    }

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
        reader.init()
        reader.listen { location ->
            _state.value = location
            when(location) {
                is GPSUseCaseImpl.GPSSerialState.Error -> {
                    println("Error: ${location.exception.message?:"What???"}")
                }
                GPSUseCaseImpl.GPSSerialState.NoPosition -> {
                    println("No position")
                }
                is GPSUseCaseImpl.GPSSerialState.Success,is GPSUseCaseImpl.GPSSerialState.Updated-> {
                    println(location)
                }
                is GPSUseCaseImpl.GPSSerialState.Message -> {
                    println(location.message)
                }
            }
        }

        val inetAddress = InetAddress.getByName(ip)
        fun send(latitude: String, longitude: String) {
            if(latitude == "*" || longitude == "*") {
                print(".")
                return
            }
            val messageBytes = pingLocation(latitude.toDouble(), longitude.toDouble()).toString().toByteArray()
            try {
                val datagramPacket = DatagramPacket(messageBytes, messageBytes.size, inetAddress, port)
                socket.send(datagramPacket)
                println("Packet sent.")
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        while (true) {
            when(val serial = state.value) {
                is GPSUseCaseImpl.GPSSerialState.Error -> {
                    println(serial.exception.message?:"What???")
                }
                is GPSUseCaseImpl.GPSSerialState.Message -> {
                    println(serial.message)
                }
                is GPSUseCaseImpl.GPSSerialState.Success -> {
                    send(serial.latitude, serial.longitude)
                }
                is GPSUseCaseImpl.GPSSerialState.Updated -> {
                    send(serial.latitude, serial.longitude)
                }
                GPSUseCaseImpl.GPSSerialState.NoPosition -> Unit
            }
            sleep(5000)
        }
    }

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO
}