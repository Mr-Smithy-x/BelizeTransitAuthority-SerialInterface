package bz.busfare.rw.viewmodels

import bz.busfare.rw.BZFare
import bz.busfare.rw.base.ViewModel
import bz.busfare.rw.extensions.toLong
import bz.busfare.rw.helpers.Cryption
import bz.busfare.rw.helpers.Time
import bz.busfare.rw.io.Serial
import bz.busfare.rw.io.impl.SerialImpl
import bz.busfare.rw.models.Card
import bz.busfare.rw.models.Data
import bz.busfare.rw.models.Response
import bz.busfare.rw.models.enums.CardActivatedState
import bz.busfare.rw.models.enums.ClassicCardType
import bz.busfare.rw.models.enums.RouteCardType
import bz.busfare.rw.usecase.CardUseCase
import bz.busfare.rw.usecase.impl.CardUseCaseImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class CardState {
    data class Loading(val card: Card) : CardState()
    data class Updated(val card: Card) : CardState()
    data class Initialized(val card: Card) : CardState()
    data object Default : CardState()
    data class Error(val card: Card?, val exception: Exception) : CardState()
}

class UnoViewModel(
    private val port: String,
    internal val serial: Serial = SerialImpl(port),
    private val useCase: CardUseCase = CardUseCaseImpl(serial, BZFare.getCardService())
) : ViewModel() {

    private val _state: MutableStateFlow<CardState> = MutableStateFlow(CardState.Default)
    val state: StateFlow<CardState> get() = _state


    fun updateCard(cost: Int, oldCard: Card) = launch {
        val newAmount: Short = (oldCard.amount - 20).toShort()
        if (newAmount >= 0) {
            val newCard = oldCard.copy(amount = newAmount)
            useCase.updateCard(cost, oldCard, newCard).collect {
                when (val response = it) {
                    is Response.Error -> _state.emit(CardState.Error(oldCard, response.exception))
                    is Response.WritingToSerial -> println("Writing: ${response.serialData}")
                    Response.Loading -> _state.emit(CardState.Loading(newCard))
                    is Response.NetworkSuccess -> println("Network response success")
                    is Response.CloseSerial -> _state.emit(CardState.Updated(response.data))

                }
            }
        } else {
            _state.emit(CardState.Error(oldCard, Exception("You do not have enough money on your card")));
        }
    }

    fun clear(card: Card?) = launch {
        useCase.clear(card).collect {
            when (val response = it) {
                Response.Loading -> if (card != null) {
                    _state.emit(CardState.Loading(card))
                } else {
                    _state.emit(CardState.Default)
                }
                is Response.CloseSerial -> {
                    println(response.data)
                    _state.emit(CardState.Default)
                }
                else -> Unit
            }
        }
    }

    fun initializeCard(
        card: Card = Card(
            Long.MAX_VALUE,
            Card.Flag.createClassicCard(
                ClassicCardType.ADULT,
                RouteCardType.LOCAL_AND_DISTRICT,
                CardActivatedState.ACTIVATED
            ),
            500,
            Time.getAddYearToCurrentTimeSeconds()
        )
    ) = launch {
        useCase.initializeCard(card).collect { response ->
            when (response) {
                is Response.Error -> _state.emit(CardState.Error(card, response.exception))
                is Response.WritingToSerial -> println("Writing: ${response.serialData}")
                Response.Loading -> _state.emit(CardState.Loading(card))
                is Response.NetworkSuccess -> println("Network response success")
                is Response.CloseSerial -> _state.emit(CardState.Initialized(response.data))

            }
        }
    }

    fun printBlocks(map: HashMap<String, Data>) {
        for ((key, value) in map.toSortedMap { o1, o2 ->
            if (o1!!.toInt() < o2!!.toInt()) {
                -1
            } else {
                1
            }
        }) {
            println(
                "${key.padStart(2, '0')}: ${value.hex} - ${
                    value.byteArray.map {
                        it.toInt().toChar()
                    }
                } - ${value.byteArray.toList()}"
            )
        }
    }


    @OptIn(ExperimentalStdlibApi::class)
    fun parseCard(blocks: HashMap<String, Data>): CardState {
        try {
            val block6 = blocks["6"]
            val encodedId = blocks["4"]!!.byteArray.toHexString(HexFormat.UpperCase)
            val encodedCardInfo = blocks["5"]!!.byteArray.toHexString(HexFormat.UpperCase)

            val block4Id = Cryption.decrypt(encodedId)
            val block5info = Cryption.decrypt(encodedCardInfo)

            if (String(block6!!.byteArray) == "BZ Transit Card!") {
                val card = Card.fromBytes(block4Id?.toLong()!!, block5info!!)
                printBlocks(blocks)
                println(card.dump())
                return CardState.Initialized(card)
            } else {
                println("Failure")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return CardState.Error(null, e)
        }
        return CardState.Error(null, Exception("Card is in an unknown state"))
    }

    fun retrieveBlocks(contentToString: String): HashMap<String, Data> {
        val arr = contentToString.split("|")
        val map = hashMapOf(*arr[1].split(",").map {
            val kv = it.split(":")
            val key = kv[0].replace("Block ", "")
            val value = kv[1].trim().replace("  ", " ").split(" ").filter(String::isNotEmpty).map { v ->
                try {
                    val toByte = v.toInt(16).toByte()
                    toByte
                } catch (e: NumberFormatException) {
                    println(v)
                    throw e
                }
            }
            key to Data(kv[1].trim(), value.toByteArray())
        }.toTypedArray())
        return map
    }
}
