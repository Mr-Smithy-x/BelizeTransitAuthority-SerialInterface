package bz.busfare.rw.network

import bz.busfare.rw.models.network.BZResponse
import bz.busfare.rw.models.network.CardDTO
import retrofit2.Response

class CardSocketServiceImpl: CardService {
    override suspend fun createCard(card_id: String, card_flags: String): Response<BZResponse<CardDTO>> {

        return Response.success(200, BZResponse(null))
    }

    override suspend fun updateCard(
        hexId: String,
        card_flags: String,
        old_card_flags: String,
        cost: Int
    ): Response<BZResponse<CardDTO>> {
        return Response.success(200, BZResponse(null))
    }

    override suspend fun deleteCard(hexId: String): Response<Unit> {

        return Response.success(200, Unit)
    }
}