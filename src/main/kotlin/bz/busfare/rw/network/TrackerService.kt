package bz.busfare.rw.network

import bz.busfare.rw.models.network.BZResponse
import bz.busfare.rw.models.network.BusTracker
import bz.busfare.rw.models.network.CardDTO
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface TrackerService {


    @POST("/api/track/{bus_id}/{route}")
    @FormUrlEncoded
    @Headers(value = ["Accept: application/json"])
    suspend fun updateLocation(
        @Path("bus_id") bus_id: String,
        @Path("route") route: String,
        @Field("latitude") latitude: Double,
        @Field("longitude") longitude: Double
    ): Response<BZResponse<BusTracker>>


    @GET("/api/track/{bus_id}/{route}")
    @Headers(value = ["Accept: application/json"])
    suspend fun lastLocation(
        @Path("bus_id") bus_id: String,
        @Path("route") route: String
    ): Response<BZResponse<BusTracker>>


}