package bz.busfare.rw

import bz.busfare.rw.viewmodel.state.CardState
import bz.busfare.rw.viewmodel.UnoViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UnoMifareReader(devicePort: String) {

    private val vm: UnoViewModel = UnoViewModel(devicePort)
    private val serial get() = vm.serial

    fun listen() = CoroutineScope(Dispatchers.Default).launch {
        vm.state.collect { currentState ->
            when (currentState) {
                CardState.Default -> println("Awaiting Card")
                is CardState.Loading -> println("Executing CMD")
                is CardState.Error -> {
                    println("Error Creating Card")
                    if (currentState.card != null) {
                        println(currentState.card.dump())
                    }
                    println(currentState.exception.message)
                }
                is CardState.Initialized -> {
                    println("New card created:")
                    println(currentState.card.dump())
                }
                is CardState.Updated -> {
                    println("Card Values Updated")
                    println(currentState.card.dump())
                }
            }
        }
    }


    fun read() {
        listen()
        val sb = StringBuilder()
        var stub: String?
        while (true) {
            if (serial.ready()) {
                while (serial.ready()) {
                    stub = serial.readLine()
                    if (!stub.isNullOrEmpty()) {
                        sb.append(stub)
                    } else break

                }
                val content = sb.toString()
                sb.clear()
                stub = null
                if (content.isNotEmpty()) {
                    if (content.contains("[START]") && content.contains("[END]")) {
                        val blocks = vm.retrieveBlocks(content)
                        when (val state = vm.parseCard(blocks)) {
                            is CardState.Initialized -> {
                                val cost = Config.getInt("FARE_COST")!!
                                vm.updateCard(cost, state.card)
                            }
                            is CardState.Error -> vm.initializeCard()
                            else -> Unit
                        }
                    } else {
                        println(content)
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