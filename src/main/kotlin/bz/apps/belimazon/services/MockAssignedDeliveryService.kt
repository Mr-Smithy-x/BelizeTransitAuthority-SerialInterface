package bz.apps.belimazon.services

import bz.apps.belimazon.models.Address
import bz.apps.belimazon.models.ShippingLabel
import bz.apps.busfare.rw.models.network.BZResponse
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class MockAssignedDeliveryService : AssignedDeliveryService {


    private val instore = hashSetOf<ShippingLabel.ReceivedInStore>(
        ShippingLabel.ReceivedInStore(
            "0511-1140-7592",
            to = Address("Charlton"),
            from = Address("Anwar"),
            weight = 15f,
            expectedDate = "November 12, 2025"
        ),
        ShippingLabel.ReceivedInStore(
            "0707-3400-0034",
            to = Address("Anwar"),
            from = Address("Vashti", "198 Western Paradise Village", "8 Mile", "Belize", "Belize", "N/A"),
            weight = 12f,
            expectedDate = "November 12, 2025"
        ),
        ShippingLabel.ReceivedInStore(
            "0003-0000-000",
            to = Address("Vashti"),
            from = Address("Bernard Junior"),
            weight = 9f,
            expectedDate = "November 12, 2025"
        ),
        ShippingLabel.ReceivedInStore(
            "0004-0000-000",
            to = Address("Bernard Junior"),
            from = Address("Charlton"),
            weight = 6f,
            expectedDate = "November 12, 2025"
        )
    )


    private val shipping = hashSetOf<ShippingLabel.OutForDelivery>()

    private val delivered = hashSetOf<ShippingLabel.Delivered>()

    override suspend fun scanToOutForDelivery(shipping_id: String): Response<BZResponse<ShippingLabel.OutForDelivery>> {
        val find = instore.find { it.id == shipping_id }

        if (find == null) {

            val secondLook = shipping.find { it.id == shipping_id }

            if(secondLook != null) {
                return Response.success(226, BZResponse(secondLook))
            }

            return Response.error(404, null.toString().toResponseBody())
        }

        val data = ShippingLabel.OutForDelivery(find.id, find.to, find.from, find.weight, "https://belimazon.bz/assets/images/logo.png")
        shipping.add(data)
        instore.remove(find)
        return Response.success(BZResponse(data))
    }

    override suspend fun getDeliveries(): Response<BZResponse<List<ShippingLabel.OutForDelivery>>> {
        return Response.success(BZResponse(shipping.toList()))
    }

    override suspend fun delivered(shipping_id: String): Response<BZResponse<ShippingLabel.Delivered>> {
        val find = shipping.find { it.id == shipping_id }

        if (find == null) {
            return Response.error(404, null.toString().toResponseBody())
        }

        val data = ShippingLabel.Delivered(find.id, find.to, find.from, find.weight, "https://belimazon.bz/assets/images/logo.png")
        delivered.add(data)
        shipping.remove(find)
        return Response.success(BZResponse(data))
    }

}
