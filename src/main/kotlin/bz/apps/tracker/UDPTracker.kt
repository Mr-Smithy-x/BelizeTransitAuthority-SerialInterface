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

    private lateinit var ip: String
    private var port: Int = -1
    private val socket = DatagramSocket()
    private val _state: MutableState<GPSSerialState> = mutableStateOf(GPSSerialState.NoPosition)
    val state: State<GPSSerialState> get() = _state

    val reader by lazy {
        val string = Config.getString("GPS_PORT")
        GPSReader(string!!)
    }

    private val uuid by lazy {
        Config.getString("GPS_UUID") ?: error("UUID not found")
    }


    private fun addSerialState(serial: GPSSerialState.Updated): JsonObject {
        val element = JsonObject()
        val (latitude, longitude, speed, course, courseCardinal, satellites, hdop, altitude, datetime, age, charactersProcessed, sentencesFixed, failedCheckSum, raw) = serial
        element.addProperty("tracker_uuid", uuid)
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
        return element
    }

    private fun pingCrash(serial: GPSSerialState.Updated): JsonObject {
        val message = JsonObject().apply {
            addProperty("type", "cmd")
            addProperty("command", "crash_detection")
            add("data", addSerialState(serial))
        }
        return message
    }

    private fun addTracker(serial: GPSSerialState.Updated): JsonObject {
        val message = JsonObject().apply {
            addProperty("type", "cmd")
            addProperty("command", "add_tracker")
            add("data", addSerialState(serial))
        }
        return message
    }

    private fun pingLocation(serial: GPSSerialState.Updated): JsonObject {
        val message = JsonObject().apply {
            addProperty("type", "cmd")
            addProperty("command", "update")
            add("data", addSerialState(serial))
        }
        return message
    }

    fun send(serial: GPSSerialState.Updated): Boolean {
        val inetAddress = InetAddress.getByName(ip)
        if(serial.latitude == "*" || serial.longitude == "*") {
            return false
        }
        val messageBytes = pingLocation(serial).toString().toByteArray()
        try {
            val datagramPacket = DatagramPacket(messageBytes, messageBytes.size, inetAddress, port)
            socket.send(datagramPacket)
            if(Config.getBoolean("TESTING")) {
                datagramPacket.address = InetAddress.getByName(Config.getString("GPS_UDP_TEST_HOST")?:"127.0.0.1")
                socket.send(datagramPacket)
            }
            return true
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun sendCrash(serial: GPSSerialState.Updated): Boolean {
        val inetAddress = InetAddress.getByName(ip)
        if(serial.latitude == "*" || serial.longitude == "*") {
            return false
        }
        val messageBytes = pingCrash(serial).toString().toByteArray()
        try {
            val datagramPacket = DatagramPacket(messageBytes, messageBytes.size, inetAddress, port)
            socket.send(datagramPacket)
            if(Config.getBoolean("TESTING")) {
                datagramPacket.address = InetAddress.getByName(Config.getString("GPS_UDP_TEST_HOST")?:"127.0.0.1")
                socket.send(datagramPacket)
            }
            return true
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }


    fun start(ip: String, port: Int) = launch {
        this@UDPTracker.ip = ip
        this@UDPTracker.port = port
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
            sleep(3000)
        }
    }

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO
}