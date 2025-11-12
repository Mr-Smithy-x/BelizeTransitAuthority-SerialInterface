package bz.apps.belimazon.services

import bz.apps.belimazon.models.ShippingLabel
import bz.apps.busfare.rw.models.network.BZResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface AssignedDeliveryService {

    @GET("/api/courier/deliveries")
    @Headers(value = ["Accept: application/json"])
    suspend fun getDeliveries(): Response<BZResponse<List<ShippingLabel.OutForDelivery>>>

    @POST("/api/courier/deliveries/{shipping_id}/delivered")
    @Headers(value = ["Accept: application/json"])
    suspend fun delivered(@Path("shipping_id") shipping_id: String): Response<BZResponse<ShippingLabel.Delivered>>

    suspend fun scanToOutForDelivery(shipping_id: String): Response<BZResponse<ShippingLabel.OutForDelivery>>
}
