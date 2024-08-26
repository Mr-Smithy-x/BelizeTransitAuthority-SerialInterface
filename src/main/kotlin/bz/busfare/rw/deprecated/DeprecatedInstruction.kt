package bz.busfare.rw.deprecated

import bz.busfare.rw.BZFare
import bz.busfare.rw.viewmodel.state.CardState
import bz.busfare.rw.helpers.Time
import bz.busfare.rw.io.Serial
import bz.busfare.rw.models.Card
import bz.busfare.rw.models.enums.CardActivatedState
import bz.busfare.rw.models.enums.ClassicCardType
import bz.busfare.rw.models.enums.RouteCardType
import kotlinx.coroutines.*
import java.awt.Toolkit

@Deprecated("No longer in use")
object Instruction {

    lateinit var serial: Serial

    fun sendNewCardState(
        card: Card
    ): Deferred<Boolean> {
        return initializeNewCard(serial, card)
    }


    fun update(newCardInfo: Card, oldCardInfo: Card, cost: Int) =
        updateCard(serial, newCardInfo, oldCardInfo, cost)


    private fun initializeCard(cardState: CardState.Error) = CoroutineScope(Dispatchers.Default).launch {
        println("BZ Transit Card not initialized. setting default card information")
        val result = sendNewCardState(
            Card(
                Long.MAX_VALUE,
                Card.Flag.createClassicCard(
                    ClassicCardType.SENIOR,
                    RouteCardType.LOCAL_AND_DISTRICT,
                    CardActivatedState.ACTIVATED
                ),
                100,
                Time.getAddYearToCurrentTimeSeconds()
            ),
        ).await()
        if(result) {
            println("INITIALIZED")
        }
    }

    private fun payCard(cardState: CardState.Initialized) = CoroutineScope(Dispatchers.Default).launch {
        val card = cardState.card
        System.err.println("Reading BZ Card")
        val cost = 20
        val newCardInfo: Card = card.copy(amount = (card.amount - cost).toShort())
        val result = update(newCardInfo, card, cost).await()
        if(result) {
            println("PAYED")
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun initializeNewCard(out: Serial, card: Card) =
        CoroutineScope(Dispatchers.Unconfined).async {
            out.write(3)
            val encryptedCardDetails = card.encryptedInfoHexString()
            val cardService = BZFare.getCardService()
            val response = cardService.createCard(encryptedCardDetails, encryptedCardDetails)
            val body = response.body()
            val data = body?.data
            if (data == null) {
                response.errorBody()?.bytes()
                println("No data found")
                return@async false
            }
            val toCard = data.toCard()
            out.write(toCard.encryptedId())
            out.write(toCard.encryptedInfo())
            out.write("BZ Transit Card!")
            Toolkit.getDefaultToolkit().beep();
            return@async true
        }


    fun updateCard(out: Serial, newCardInfo: Card, oldCardInfo: Card, cost: Int): Deferred<Boolean> =
        CoroutineScope(Dispatchers.Unconfined).async {
            out.write(2)
            val encryptedCardDetails = newCardInfo.encryptedInfoHexString()
            val oldEncryptedCardDetails = oldCardInfo.encryptedInfoHexString()
            val encryptedCardId = newCardInfo.encryptedIdHexString()

            val cardService = BZFare.getCardService()
            val response =
                cardService.updateCard(encryptedCardId, encryptedCardDetails, oldEncryptedCardDetails, cost)

            val body = response.body()
            val data = body?.data
            if (data == null) {
                println("No data found")
                return@async false
            }

            val toCard = data.toCard()
            val networkCardBytes = toCard.encryptedInfo()
            out.write(networkCardBytes)
            Toolkit.getDefaultToolkit().beep();
            return@async true
        }

}


