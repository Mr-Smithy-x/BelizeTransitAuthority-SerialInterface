package bz.apps.obdii.viewmodel

import bz.apps.busfare.rw.io.impl.SerialImpl
import bz.base.ViewModel
import com.github.eltonvs.obd.command.engine.RPMCommand
import com.github.eltonvs.obd.connection.ObdDeviceConnection
import kotlinx.coroutines.launch

class OBDIIViewModel : ViewModel() {

    lateinit var serial: SerialImpl
    lateinit var obdDeviceConnection: ObdDeviceConnection

    fun start() {

        /*serial = SerialImpl("/dev/tty.Bluetooth-Incoming-Port", 9600)
        serial.initConnection()
        obdDeviceConnection = ObdDeviceConnection(serial.input, serial.out)*/
    }

    fun test() = launch {
        val rpm = obdDeviceConnection.run(RPMCommand(), delayTime = 5000L)
        println(message = "RPM Value: " + rpm.value)
        println(message = "Unit: " + rpm.unit)
        println(message = "Formatted: " + rpm.formattedValue)
        println(message = "Raw: " + rpm.rawResponse)
    }

    fun stop() {
        serial.closePort()
    }
}