package bz.busfare.rw

import bz.busfare.rw.viewmodels.CardState
import bz.busfare.rw.viewmodels.UnoViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val FARE_COST: Short = 2

class UnoMifareReader(devicePort: String) {

    private val vm: UnoViewModel = UnoViewModel(devicePort)
    private val serial get() = vm.serial

    fun read() {
        CoroutineScope(Dispatchers.Default).launch {
            vm.state.collect { currentState ->
                when (currentState) {
                    CardState.Default -> {
                        println("Awaiting Card")
                    }
                    is CardState.Error -> {
                        println("Error Creating Card")
                        if(currentState.card != null) {
                            println(currentState.card.dump())
                        }
                        println(currentState.exception.message)
                    }
                    is CardState.Initialized -> {
                        println("New card created:")
                        println(currentState.card.dump())
                    }
                    is CardState.Loading -> {
                        println("Executing CMD")
                    }
                    is CardState.Updated -> {
                        println("Card Values Updated")
                        println(currentState.card.dump())
                    }
                }
            }
        }
        val sb = StringBuilder()
        while (true) {
            if (serial.ready()) {
                var stub: String?
                while (serial.ready()) {
                    stub = serial.readLine()
                    if (!stub.isNullOrEmpty()) {
                        sb.append(stub)
                    } else break

                }
                val content = sb.toString()
                sb.clear()
                if (content.isNotEmpty()) {
                    //if (!content.startsWith("[START]"))
                    println(content)
                    if (content.contains("[START]") && content.contains("[END]")) {
                        val blocks = vm.retrieveBlocks(content)
                        when (val state = vm.parseCard(blocks)) {
                            is CardState.Initialized -> vm.updateCard (20, state.card) //payCard(state)
                            is CardState.Error -> vm.initializeCard()
                            else -> Unit
                        }
                    }
                }
                Thread.sleep(2000)
            }
        }
    }

    fun open(): Boolean {
        val openPort = serial.openPort()
        if (openPort) {
            println("Port is open :)")
        } else {
            println("Failed to open port :(")
        }
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

}