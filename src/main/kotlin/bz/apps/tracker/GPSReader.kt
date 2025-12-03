package bz.apps.tracker

import bz.apps.tracker.usecase.state.GPSSerialState
import bz.apps.tracker.viewmodel.GPSViewModel
import kotlinx.coroutines.*

class GPSReader(devicePort: String) {

    val isOpened: Boolean get() = serial.isOpened
    private val vm: GPSViewModel = GPSViewModel(devicePort)
    private val serial get() = vm.serial

    fun listen(callback: (GPSSerialState, CoroutineScope) -> Unit) =
        CoroutineScope(Dispatchers.Default + SupervisorJob()).launch {
            vm.state.collect { currentState ->
                callback(currentState, this)
            }
        }


    fun open(opened: (Boolean) -> Unit): Boolean {
        val openPort = serial.openPort()
        if (openPort) {
            println("Port is open :)")
        } else {
            println("Failed to open port :(")
        }
        opened(openPort)
        return openPort
    }

    fun close(): Boolean {
        val closePort = serial.closePort()
        if (closePort) {
            println("Port is closed :)")
        } else {
            println("Failed to close port :(")
        }
        return closePort
    }

    fun init(): Job {
        vm.serial.initConnection()
        return vm.run()
    }

}