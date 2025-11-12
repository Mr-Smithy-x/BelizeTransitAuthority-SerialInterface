package bz.apps.belimazon.models

sealed class ShippingLabel(
    open val id: String,
    open val to: Address = Address(),
    open val from: Address = Address(),
    open val weight: Float = 0f
) {
    data class Created(override val id: String): ShippingLabel(id)

    data class NoStatus(
        override val id: String,
        override val to: Address = Address(),
        override val from: Address = Address(),
        override val weight: Float = 0f
    ): ShippingLabel(id, to, from, weight)

    data class ReceivedInStore(
        override val id: String,
        override val to: Address = Address(),
        override val from: Address = Address(),
        override val weight: Float = 0f,
        val expectedDate: String,
    ): ShippingLabel(id, to, from, weight)

    data class OutForDelivery(
        override val id: String,
        override val to: Address = Address(),
        override val from: Address = Address(),
        override val weight: Float = 0f,
        val expectedDate: String,
    ): ShippingLabel(id, to, from, weight)

    data class Delivered(
        override val id: String,
        override val to: Address = Address(),
        override val from: Address = Address(),
        override val weight: Float = 0f,
        val attached: String,
    ): ShippingLabel(id, to, from, weight)
}