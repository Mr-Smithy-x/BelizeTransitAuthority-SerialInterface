package bz.busfare.rw

import bz.busfare.rw.extensions.toByteArray
import bz.busfare.rw.extensions.toLong
import bz.busfare.rw.helpers.Cryption.decrypt
import bz.busfare.rw.helpers.Cryption.encrypt
import bz.busfare.rw.helpers.Time
import bz.busfare.rw.models.Card
import bz.busfare.rw.models.enums.CardActivatedState
import bz.busfare.rw.models.enums.RouteCardType
import bz.busfare.rw.models.enums.StudentCardType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException


val pr: (String) -> Unit = ::println

@Throws(IOException::class, InterruptedException::class)
fun main() {
    val reader = UnoMifareReader("COM4")
    reader.open()
    reader.read()
}


fun sample() {
    /*val b = 0b0000_0101
    val c = 0b1111_1111
    val d = b.xor(c)
    val output = Integer.toBinaryString(d).padStart(8, '0').chunked(4).joinToString(" ")


    val key = "Bt43zC4rDTr4nZ1T".toByteArray() // 16 bytes key for AES-128
    val iv = "ILoveBelize!2024".toByteArray()  // 16 bytes initialization vector
    val plaintext = to16Bytes.second.toHexString(HexFormat.UpperCase)

    val encrypted = encryptHex(plaintext)
    val decrypted = decryptHex(encrypted)

    println(encrypted)
    println(decrypted)


    println(plaintext.equals(decrypted))
    println(to16Bytes.second)
    to16Bytes.second.toByteArray().toLong()
    */
    val card = Card(
        Long.MAX_VALUE,
        Card.Flag.createStudentCard(
            StudentCardType.COLLEGE,
            RouteCardType.LOCAL,
            CardActivatedState.ACTIVATED
        ),
        200,
        Time.getAddYearToCurrentTimeSeconds()
    )

    val to16Bytes = card.to16BytesArrayPair()
    pr("----------------------------------------------------------")
    val enc = encrypt(to16Bytes.second)
    val dec = decrypt(enc)?.toLong()

    pr(enc!!)
    println(dec)
    println(Card.fromBytes(0, dec!!.toByteArray()))
    //pr(decrypted)


    //val plaintext = second
    //val encrypted = encrypt(plaintext)
    //pr("Encrypted: $encrypted")
    //val decrypted = decrypt(encrypted)
    //pr("Decrypted: $decrypted")

    //7B50502BC5BE1DD1E95EB411363CC199
    //340963412062
    //0000004F6300C85E
    CoroutineScope(Dispatchers.Unconfined).launch {

        val cardService = BZFare.getCardService()
        val response = cardService.createCard(enc!!, enc!!)

        val body = response.body()
        val data = body?.data ?: run {
            println("No data found")
        }
        println(data)

    }
}