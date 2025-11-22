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
        "datetime:11/21/2025 22:50:03".split(":", limit=2).forEach {
            println(it)
        }
    }

}