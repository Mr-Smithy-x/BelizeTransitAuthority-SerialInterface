package bz.tracker

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class GPSReader(devicePort: String) {

    val isOpened: Boolean get() = serial.isOpened
    private val vm: GPSViewModel = GPSViewModel(devicePort)
    private val serial get() = vm.serial

    fun listen(callback: (GPSViewModel.GPSState) -> Unit) =
        CoroutineScope(Dispatchers.Default + SupervisorJob()).launch {
            vm.state.collect { currentState ->
                callback(currentState)
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

    fun init() {
        vm.serial.initConnection()
    }

}