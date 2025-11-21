package bz.apps.busfare.rw.io

import bz.apps.busfare.rw.io.enums.SerialCmd
import bz.apps.busfare.rw.io.enums.SerialResponse
import com.fazecast.jSerialComm.SerialPort

interface Serial {
    val isOpened: Boolean
    val port: SerialPort
    val devicePort: String
    val baudRate: Int
    fun write(data: ByteArray)
    fun write(data: Int)
    fun write(cmd: SerialCmd) = write(cmd.ordinal)
    fun write(data: String)
    fun readLine(): String?
    fun readSerialResponse(): SerialResponse
    fun ready(): Boolean
    fun openPort(): Boolean
    fun closePort(): Boolean
    fun initConnection()
    val isInitialized: Boolean
}