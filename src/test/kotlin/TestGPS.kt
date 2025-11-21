import bz.Config
import bz.apps.busfare.rw.helpers.Cryption
import bz.apps.busfare.rw.helpers.Time
import bz.apps.busfare.rw.models.Card
import bz.apps.busfare.rw.models.enums.*
import bz.apps.busfare.rw.models.network.CardDTO
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TestGPS {

    @BeforeEach
    fun setUp() {
        Config.load(".env.properties")
    }

    @Test
    fun testOK() {
        println(Time.getAddYearToCurrentTimeSeconds())
    }

    @Test
    fun testOK1() {
        val expiryDate = Time.getAddYearToCurrentTimeSeconds()
        val card = Card(
            Long.MAX_VALUE,
            Card.Flag.createStudentCard(
                StudentCardType.COLLEGE,
                RouteCardType.LOCAL,
                CardActivatedState.ACTIVATED
            ),
            200,
            expiryDate
        )
        //var encrypted = "C82D31FF4456D9844DB4AF095CADFA4C"
        //val encrypted = "126285E53C5AC958640417D2C2D6B47A"
        val bytes = card.to16BytesArrayPair().second
        val encrypted = Cryption.encrypt(bytes)
        val cardInfo = Cryption.decrypt(encrypted)!!
        val decrypted = Card.fromBytes(Long.MAX_VALUE, cardInfo)
        assert(card.uid == decrypted.uid)
        return
    }

    @Test
    fun testOK2() {
        val cardDTO = CardDTO(
            Long.MAX_VALUE,
            amount = 100,
            expiryDate = 1756080000,
            cardType = CardType.CLASSIC,
            classicCardType = ClassicCardType.SENIOR,
            subscriptionCardType = null,
            studentCardType = null,
            municipalCardType = null,
            routeCardType = RouteCardType.LOCAL_AND_DISTRICT,
            cardActivatedState = CardActivatedState.ACTIVATED
        )
        assert(cardDTO.id == cardDTO.toCard().uid)
    }
}