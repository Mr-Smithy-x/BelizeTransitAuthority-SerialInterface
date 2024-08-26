package bz.busfare.rw.io

import bz.busfare.rw.io.enums.SerialCmd
import bz.busfare.rw.io.enums.SerialResponse
import com.fazecast.jSerialComm.SerialPort

interface Serial {
    val port: SerialPort
    fun write(data: ByteArray)
    fun write(data: Int)
    fun write(cmd: SerialCmd) = write(cmd.ordinal)
    fun write(data: String)
    fun readLine(): String?
    fun readSerialResponse(): SerialResponse
    fun ready(): Boolean
    fun openPort(): Boolean
    fun closePort(): Boolean
}