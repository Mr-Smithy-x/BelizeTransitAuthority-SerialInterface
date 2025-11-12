package bz.apps.busfare.rw

import androidx.compose.runtime.mutableStateOf
import bz.apps.busfare.rw.models.Location
import bz.apps.busfare.rw.viewmodel.state.CardState

object Stats {

    val currentRoute = mutableStateOf<Pair<Location, Location>?>(null)
    val lastLocation = mutableStateOf<Location?>(null)

    val map = hashMapOf<Long, String>()
    val count = hashMapOf<Long, Int>()
    val failureCount = hashMapOf<Long, Int>()
    fun saveLastScannedCard(cardState: CardState) {
        when (cardState) {
            is CardState.Initialized -> {
                map[cardState.card.uid] = cardState.card.encryptedInfoHexString()
                count[cardState.card.uid] = count.getOrDefault(cardState.card.uid, 0) + 1
            }
            is CardState.Updated -> {
                map[cardState.card.uid] = cardState.card.encryptedInfoHexString()
                count[cardState.card.uid] = count.getOrDefault(cardState.card.uid, 0) + 1
            }
            is CardState.Error -> {
                if (cardState.card != null) {
                    map[cardState.card.uid] = cardState.card.encryptedInfoHexString()
                    failureCount[cardState.card.uid] = failureCount.getOrDefault(cardState.card.uid, 0) + 1
                }
            }
            else -> Unit
        }
    }

}