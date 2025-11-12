package bz.apps.busfare.rw.io.impl

import bz.apps.busfare.rw.io.Serial
import bz.apps.busfare.rw.io.enums.SerialResponse
import com.fazecast.jSerialComm.SerialPort

class SerialImpl(devicePort: String): Serial {

    override val isOpened: Boolean
        get() = port.isOpen
    override val port: SerialPort = SerialPort.getCommPort(devicePort)
    private val out get() = port.outputStream
    private val input get() = port.inputStream
    private val reader = input.bufferedReader()

    init {
        port.setComPortParameters(9600, 8, 1, 0) // default connection settings for Arduino
        port.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0) // block until bytes can be written
    }

    override fun readLine(): String = reader.readLine()
    override fun readSerialResponse(): SerialResponse {
        val response = input.read()
        println("Serial response: $response")
        return SerialResponse.entries[response]
    }


    override fun ready(): Boolean {
        return reader.ready()
    }

    override fun openPort(): Boolean = port.openPort()
    override fun closePort(): Boolean {
        port.flushIOBuffers()
        val closePort = port.closePort()
        return closePort
    }

    override fun write(data: ByteArray) {
        out.write(data)
        out.flush()
    }

    override fun write(data: Int) {
        out.write(data)
        out.flush()
    }

    override fun write(data: String) = write(data.toByteArray())
}