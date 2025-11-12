package bz.apps.busfare.rw.network

import bz.apps.busfare.rw.models.network.BZResponse
import bz.apps.busfare.rw.models.network.CardDTO
import retrofit2.Response
import retrofit2.http.*

interface CardService {

    @POST("/api/cards")
    @FormUrlEncoded
    @Headers(value = ["Accept: application/json"])
    suspend fun createCard(@Field("card_id") card_id: String, @Field("card_flags") card_flags: String): Response<BZResponse<CardDTO>>

    @PATCH("/api/cards/{hexId}")
    @FormUrlEncoded
    @Headers(value = ["Accept: application/json"])
    suspend fun updateCard(@Path("hexId") hexId: String, @Field("card_flags") card_flags: String, @Field("old_card_flags") old_card_flags: String, @Field("cost") cost: Int): Response<BZResponse<CardDTO>>


    @DELETE("/api/cards/{hexId}")
    @FormUrlEncoded
    @Headers(value = ["Accept: application/json"])
    suspend fun deleteCard(@Path("hexId") hexId: String): Response<Unit>

}