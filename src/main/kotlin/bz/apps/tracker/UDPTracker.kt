package bz.apps.tracker

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import bz.Config
import bz.apps.tracker.usecase.state.GPSSerialState
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
    private val _state: MutableState<GPSSerialState> = mutableStateOf(GPSSerialState.NoPosition)
    val state: State<GPSSerialState> get() = _state

    val reader by lazy {
        val string = Config.getString("PORT")
        GPSReader(string!!)
    }

    private val uuid by lazy {
        Config.getString("UUID") ?: error("UUID not found")
    }

    private fun pingLocation(serial: GPSSerialState.Updated): JsonObject {
        val message = JsonObject().apply {
            addProperty("type", "cmd")
            addProperty("command", "update")
            addProperty("uuid", uuid)
            val element = JsonObject()
            val (latitude, longitude, speed, course, courseCardinal, satellites, hdop, altitude,  datetime, age, charactersProcessed, sentencesFixed, failedCheckSum) = serial
            element.addProperty("latitude", latitude.toDouble())
            element.addProperty("longitude", longitude.toDouble())
            element.addProperty("speed", speed.toDouble())
            element.addProperty("course", course.toDouble())
            element.addProperty("course_cardinal", courseCardinal)
            element.addProperty("satellites", satellites.toInt())
            element.addProperty("hdop", hdop.toDouble())
            element.addProperty("altitude", altitude.toDouble())
            element.addProperty("datetime", datetime)
            element.addProperty("age", age.toInt())
            element.addProperty("characters_processed", charactersProcessed.toInt())
            element.addProperty("sentences_fixed", sentencesFixed.toInt())
            element.addProperty("failed_checksum", failedCheckSum.toInt())
            add("data", element)
        }
        return message
    }

    fun start(ip: String, port: Int) = launch {
        reader.init()
        reader.listen { location ->
            _state.value = location
            when(location) {
                is GPSSerialState.Error -> {
                    println("Error: ${location.exception.message?:"What???"}")
                }
                GPSSerialState.NoPosition -> {
                    println("No position")
                }
                is GPSSerialState.Positioning -> {
                    println("Finding position...")
                }
                is GPSSerialState.Updated-> {
                    //println(location)
                }
                is GPSSerialState.Message -> {
                    println(location.message)
                }
            }
        }

        val inetAddress = InetAddress.getByName(ip)
        fun send(serial: GPSSerialState.Updated): Boolean {
            if(serial.latitude == "*" || serial.longitude == "*") {
                return false
            }
            val messageBytes = pingLocation(serial).toString().toByteArray()
            try {
                val datagramPacket = DatagramPacket(messageBytes, messageBytes.size, inetAddress, port)
                socket.send(datagramPacket)
                return true
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }

        while (true) {
            when(val serial = state.value) {
                is GPSSerialState.Error -> Unit
                is GPSSerialState.Message -> Unit
                is GPSSerialState.Positioning -> {
                    //TODO: Wont update because its old location data or its finding a position
                }
                is GPSSerialState.Updated -> {
                    if(send(serial)) {
                        println("sent:$serial")
                    }
                }
                GPSSerialState.NoPosition -> Unit
            }
            sleep(5000)
        }
    }

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO
}