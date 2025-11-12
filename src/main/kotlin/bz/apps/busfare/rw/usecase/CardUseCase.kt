package bz.apps.busfare.rw.usecase

import bz.apps.busfare.rw.models.Response
import bz.apps.busfare.rw.models.Card
import kotlinx.coroutines.flow.Flow

interface CardUseCase {

    fun initializeCard(card: Card): Flow<Response<Card>>
    fun updateCard(cost: Int, oldCard: Card, newCard: Card): Flow<Response<Card>>
    fun clear(card: Card?): Flow<Response<String>>
}