package bz.busfare.rw.usecase

import bz.busfare.rw.models.Response
import bz.busfare.rw.models.Card
import kotlinx.coroutines.flow.Flow

interface CardUseCase {

    fun initializeCard(card: Card): Flow<Response<Card>>
    fun updateCard(cost: Int, oldCard: Card, newCard: Card): Flow<Response<Card>>
    fun clear(card: Card?): Flow<Response<String>>
}