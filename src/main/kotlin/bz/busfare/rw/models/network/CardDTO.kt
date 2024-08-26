package bz.busfare.rw.models.network

import bz.busfare.rw.models.Card
import bz.busfare.rw.models.enums.*
import com.google.gson.annotations.SerializedName

data class CardDTO(
    val id: Long,
    @SerializedName("amount")
    val amount: Short,
    @SerializedName("expiry_date")
    val expiryDate: Long,
    @SerializedName("card_type")
    val cardType: CardType = CardType.CLASSIC,
    @SerializedName("classic_type")
    val classicCardType: ClassicCardType? = null,
    @SerializedName("subscription_type")
    val subscriptionCardType: SubscriptionCardType? = null,
    @SerializedName("student_type")
    val studentCardType: StudentCardType? = null,
    @SerializedName("municipal_type")
    val municipalCardType: MunicipalCardType? = null,
    @SerializedName("route_type")
    val routeCardType: RouteCardType = RouteCardType.NONE,
    @SerializedName("activation")
    val cardActivatedState: CardActivatedState = CardActivatedState.NOT_ACTIVATED
) {
    fun toCard(): Card {
        return Card(id, when(cardType) {
            CardType.CLASSIC -> Card.Flag.createClassicCard(classicCardType!!, routeCardType, cardActivatedState)
            CardType.SUBSCRIPTION -> Card.Flag.createSubscriptionCard(subscriptionCardType!!, routeCardType, cardActivatedState)
            CardType.STUDENT -> Card.Flag.createStudentCard(studentCardType!!, routeCardType, cardActivatedState)
            CardType.MUNICIPAL -> Card.Flag.createMunicipalCard(municipalCardType!!, routeCardType, cardActivatedState)
        }, amount, expiryDate)
    }
}