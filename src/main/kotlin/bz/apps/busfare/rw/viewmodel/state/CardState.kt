package bz.apps.busfare.rw.viewmodel.state

import bz.apps.busfare.rw.models.Card

sealed class CardState {
    data class Loading(val card: Card) : CardState()
    data class Updated(val card: Card) : CardState()
    data class Initialized(val card: Card) : CardState()
    data object Default : CardState()
    data class Error(val card: Card?, val exception: Exception) : CardState()
}