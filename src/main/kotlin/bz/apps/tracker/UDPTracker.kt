package bz.apps.tracker

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import bz.Config
import bz.apps.tracker.usecase.state.GPSSerialState
import com.google.gson.JsonObject
import io.github.davidepianca98.MQTTClient
import io.github.davidepianca98.mqtt.MQTTVersion
import io.github.davidepianca98.mqtt.Subscription
import io.github.davidepianca98.mqtt.packets.Qos
import io.github.davidepianca98.mqtt.packets.mqttv5.SubscriptionOptions
import kotlinx.coroutines.*
import java.lang.Thread.sleep
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlin.coroutines.CoroutineContext

object UDPTracker : CoroutineScope {

    private lateinit var ip: String
    private var port: Int = -1
    private val socket = DatagramSocket()

    @OptIn(ExperimentalUnsignedTypes::class)
    lateinit var client: MQTTClient
    //private val _state: MutableState<GPSSerialState> = mutableStateOf(GPSSerialState.NoPosition)
    private val _state: MutableState<GPSSerialState> = mutableStateOf(GPSSerialState.Updated(
        latitude = 40.87466.toString(),
        longitude = (-73.894889).toString(),
        speed = 6.39.toString(),
        course = 223.98.toString(),
        courseCardinal = "SW",
        satellites = 10.toString(),
        hdop = 0.9.toString(),
        altitude = 40.7.toString(),
        datetime = "2025-12-03 02:43:09",
        age = 729.toString(),
        charactersProcessed = 0.toString(),
        sentencesFixed = 0.toString(),
        failedCheckSum = 0.toString(),
        raw = ""
    ))
    //
    val state: State<GPSSerialState> get() = _state

    val list = arrayListOf<Job>()

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
        val job = reader.init()
        reader.listen { location, scope ->
            if(!isActive) {
                scope.cancel()
            }
            scope.ensureActive()
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
            ensureActive()
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

    @OptIn(ExperimentalUnsignedTypes::class)
    fun startMQTT() = launch {
        client = MQTTClient(
            MQTTVersion.MQTT3_1_1,
            "iot.charltonsmith.nyc",
            1883,
            null,
            onConnected = {
                println("Connected")
            },
            onDisconnected = {
                println("Disconnected")
            }
        ) {
            println(it.payload?.toByteArray()?.decodeToString())
        }

        val job = reader.init()
        reader.listen { location, scope ->
            if(!isActive) {
                scope.cancel()
            }
            scope.ensureActive()
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
            ensureActive()
            when(val serial = state.value) {
                is GPSSerialState.Error -> Unit
                is GPSSerialState.Message -> Unit
                is GPSSerialState.Positioning -> {
                    //TODO: Wont update because its old location data or its finding a position
                }
                is GPSSerialState.Updated -> {
                    sendMTTQ(serial)
                }
                GPSSerialState.NoPosition -> Unit
            }
            sleep(3000)
        }
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    fun sendMTTQ(serial: GPSSerialState.Updated) {
        client.publish(false, Qos.EXACTLY_ONCE, "tracker/update", addSerialState(serial).toString().encodeToByteArray().toUByteArray())
        client.step() // Blocking method, use step() if you don't want to block the thread
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    fun sendMTTQCrash(serial: GPSSerialState.Updated) {
        client.publish(false, Qos.EXACTLY_ONCE, "tracker/crash", addSerialState(serial).toString().encodeToByteArray().toUByteArray())
        client.step() // Blocking method, use step() if you don't want to block the thread
    }

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO
}