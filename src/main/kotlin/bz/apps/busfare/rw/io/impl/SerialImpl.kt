package bz.apps.busfare.rw.io.impl

import bz.apps.busfare.rw.io.Serial
import bz.apps.busfare.rw.io.enums.SerialResponse
import com.fazecast.jSerialComm.SerialPort
import java.io.BufferedReader

class SerialImpl(override val devicePort: String, override val baudRate: Int = 9600) : Serial {

    override lateinit var port: SerialPort
    private lateinit var reader: BufferedReader

    val out get() = port.outputStream
    val input get() = port.inputStream
    override val isInitialized get() = this::port.isInitialized
    override val isOpened: Boolean get() = if (this::port.isInitialized) port.isOpen else false


    override fun initConnection() {

        port = SerialPort.getCommPort(devicePort)
        port.setComPortParameters(baudRate, 8, 1, 0) // default connection settings for Arduino
        port.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0) // block until bytes can be written
        port.openPort()
        reader = input.bufferedReader()
    }

    override fun readLine(): String = reader.readLine()

    override fun readSerialResponse(): SerialResponse {
        if(!isInitialized || !isOpened) throw IllegalStateException("Serial port is not initialized or not opened")
        val response = input.read()
        println("Serial response: $response")
        return SerialResponse.entries[response]
    }


    override fun ready(): Boolean {
        if(!isInitialized) return false
        return reader.ready()
    }

    override fun openPort(): Boolean = if(isInitialized) port.openPort() else false
    override fun closePort(): Boolean {
        if(!isInitialized) return true
        port.flushIOBuffers()
        val closePort = port.closePort()
        return closePort
    }

    override fun write(data: ByteArray) {
        if(!isInitialized) return
        out.write(data)
        out.flush()
    }

    override fun write(data: Int) {
        if(!isInitialized) return
        out.write(data)
        out.flush()
    }

    override fun write(data: String) = write(data.toByteArray())
}