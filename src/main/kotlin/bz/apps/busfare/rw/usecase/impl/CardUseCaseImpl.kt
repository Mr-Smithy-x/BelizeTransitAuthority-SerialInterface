package bz.apps.busfare.rw.usecase.impl

import bz.apps.busfare.rw.io.Serial
import bz.apps.busfare.rw.io.enums.SerialCmd
import bz.apps.busfare.rw.io.enums.SerialResponse
import bz.apps.busfare.rw.models.Card
import bz.apps.busfare.rw.models.Response
import bz.apps.busfare.rw.network.CardService
import bz.apps.busfare.rw.usecase.CardUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CardUseCaseImpl(private val serial: Serial, private val cardService: CardService) : CardUseCase {

    // output 3
    override fun clear(card: Card?): Flow<Response<String>> = flow {
        emit(Response.Loading)
        if (card != null) {
            /*  val response = cardService.createCard(card.encryptedIdHexString(), card.encryptedInfoHexString())
                val body = response.body()
                val data = body?.data
                if (data == null) {
                    serial.write(1)
                    emit(Response.Error(Exception("No data found")))
                    return@flow
                }
            */
        }
        serial.write(SerialCmd.CLEAR_DATA)
        emit(Response.CloseSerial("Cleared Data On Serial"))
    }

    // output 3
    override fun initializeCard(card: Card): Flow<Response<Card>> = flow {
        emit(Response.Loading)
        val response = cardService.createCard(card.encryptedIdHexString(), card.encryptedInfoHexString())
        val body = response.body()
        val data = body?.data
        if (data == null) {
            serial.write(1)
            emit(Response.Error(Exception("No data found")))
            return@flow
        }
        val newCard = data.toCard()
        emit(Response.NetworkSuccess(card))
        serial.write(SerialCmd.INITIALIZE)
        emit(Response.WritingToSerial(newCard, newCard.encryptedIdHexString()))
        serial.write(newCard.encryptedId())
        var serialResponse = serial.readSerialResponse()
        if (serialResponse == SerialResponse.SUCCEEDED) {
            emit(Response.WritingToSerial(newCard, newCard.encryptedInfoHexString()))
            serial.write(newCard.encryptedInfo())
            serialResponse = serial.readSerialResponse()
            if (serialResponse == SerialResponse.SUCCEEDED) {
                val serialInitData = "BZ Transit Card!"
                emit(Response.WritingToSerial(newCard, serialInitData))
                serial.write(serialInitData)
                serialResponse = serial.readSerialResponse()
                if (serialResponse == SerialResponse.SUCCEEDED) {
                    emit(Response.CloseSerial(newCard))
                } else {
                    emit(Response.Error(Exception("Failed at writing init to block 6")))
                }
            } else {
                emit(Response.Error(Exception("Failed at writing info to block 5")))
            }
        } else {
            emit(Response.Error(Exception("Failed at writing id to block 4")))
        }
    }

    override fun updateCard(cost: Int, oldCardInfo: Card, newCardInfo: Card): Flow<Response<Card>> = flow {
        emit(Response.Loading)
        val response = cardService.updateCard(
            oldCardInfo.encryptedIdHexString(),
            newCardInfo.encryptedInfoHexString(),
            oldCardInfo.encryptedInfoHexString(),
            cost
        )
        val body = response.body()
        val data = body?.data
        if (data == null) {
            serial.write(1)
            emit(Response.Error(Exception("No data found")))
            return@flow
        }
        val toCard = data.toCard()
        emit(Response.NetworkSuccess(toCard))
        serial.write(SerialCmd.UPDATE)
        val encryptedInfo = toCard.encryptedInfo()
        serial.write(encryptedInfo)
        emit(Response.WritingToSerial(toCard, toCard.encryptedInfoHexString()))
        if (serial.readSerialResponse() == SerialResponse.SUCCEEDED) {
            emit(Response.CloseSerial(toCard))
        } else {
            emit(Response.Error(Exception("Failed to writing info response from api")))
        }
    }

}