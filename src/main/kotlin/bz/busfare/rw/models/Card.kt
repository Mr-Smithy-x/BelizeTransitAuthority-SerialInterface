package bz.busfare.rw.models

import bz.busfare.rw.extensions.toByteArray
import bz.busfare.rw.extensions.toLong
import bz.busfare.rw.helpers.Cryption.encrypt
import bz.busfare.rw.helpers.Time
import bz.busfare.rw.models.enums.*
import java.nio.ByteBuffer

data class Card(
    val uid: Long, // 64 bits // 8 bytes
    val flag: Flag, // 8 bits // 1 byte
    val amount: Short, // 16 bits // 2 bytes
    // store in seconds, if by chance im not around in the year 36,866.136, use minutes instead of seconds
    var expiryDate: Long, // 40 bits // 5 bytes
) {

    fun to16BytesLong(): Pair<Long, Long> {
        var flagAmountExpiry: Long = flag.toByte().toLong()
        flagAmountExpiry = flagAmountExpiry or (amount.toLong() shl 8)
        //println(((expiryDate / 10000L) shl 24).toString(2))
        flagAmountExpiry = flagAmountExpiry or ((expiryDate and 0xFFFF_FFFF_FF) shl 24)
        return Pair(uid, flagAmountExpiry)
    }

    fun to16BytesArrayPair(): Pair<ByteArray, ByteArray> {
        val to16Bytes = to16BytesLong()
        return Pair(to16Bytes.first.toByteArray(), to16Bytes.second.toByteArray())
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun encryptedId(): ByteArray {
        val clearByteCardId = uid.toByteArray()
        val bufferedBytes = ByteBuffer.wrap(ByteArray(16))
        val idEncrypted = encrypt(clearByteCardId)!!.hexToByteArray()
        bufferedBytes.put(idEncrypted, 0, idEncrypted.size)
        return bufferedBytes.array()
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun encryptedIdHexString(): String {
        return encryptedId().toHexString(HexFormat.UpperCase)
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun encryptedInfo(): ByteArray = encryptedInfoHexString().hexToByteArray()


    fun encryptedInfoHexString(): String {
        val to16Bytes = to16BytesArrayPair()
        val clearByteCardDetails = to16Bytes.second
        val encrypt = encrypt(clearByteCardDetails)!!
        return encrypt
    }

    fun dump(): String {
        return """
            --------------------------------------
            |             CARD DATA              |
            --------------------------------------
            INFO: $this
            HEX ID: ${encryptedIdHexString()}
            HEX INFO: ${encryptedInfoHexString()}
            --------------------------------------
            |           END CARD DATA            |
            --------------------------------------
        """.trimIndent()
    }

    sealed class Flag(
        open val routeCardType: RouteCardType,
        open val cardActivatedState: CardActivatedState,
        val cardType: CardType
    ) {

        data class Classic(
            val classicCardType: ClassicCardType,
            override val routeCardType: RouteCardType,
            override val cardActivatedState: CardActivatedState
        ): Flag(routeCardType, cardActivatedState, CardType.CLASSIC)

        data class Subscription(
            val subscriptionCardType: SubscriptionCardType,
            override val routeCardType: RouteCardType,
            override val cardActivatedState: CardActivatedState
        ): Flag(routeCardType, cardActivatedState, CardType.SUBSCRIPTION)

        data class Student(
            val studentCardType: StudentCardType,
            override val routeCardType: RouteCardType,
            override val cardActivatedState: CardActivatedState
        ): Flag(routeCardType, cardActivatedState, CardType.STUDENT)

        data class Municipal(
            val workerCardType: MunicipalCardType,
            override val routeCardType: RouteCardType,
            override val cardActivatedState: CardActivatedState
        ): Flag(routeCardType, cardActivatedState, CardType.MUNICIPAL)

        fun toByte(): Byte {
            var cardByte = 0

            cardByte = cardByte or cardType.ordinal
            cardByte = cardByte or (when (this) {
                is Classic -> classicCardType.ordinal
                is Municipal -> workerCardType.ordinal
                is Student -> studentCardType.ordinal
                is Subscription -> subscriptionCardType.ordinal
            } shl 2)
            cardByte = cardByte or (routeCardType.ordinal shl 4)
            cardByte = cardByte or (cardActivatedState.ordinal shl 6)

            //val result = "0b" + cardByte.toByte().toString(2).padStart(8, '0')
            return cardByte.toByte()
        }

        companion object {

            fun createClassicCard(
                classicCardType: ClassicCardType,
                routeCardType: RouteCardType,
                activation: CardActivatedState
            ): Flag.Classic {
                return Flag.Classic(classicCardType, routeCardType, activation)
            }

            fun createSubscriptionCard(
                subscriptionCardType: SubscriptionCardType,
                routeCardType: RouteCardType,
                activation: CardActivatedState
            ): Flag.Subscription {
                return Flag.Subscription(subscriptionCardType, routeCardType, activation)
            }

            fun createStudentCard(
                studentCardType: StudentCardType,
                routeCardType: RouteCardType,
                activation: CardActivatedState
            ): Flag.Student {
                return Flag.Student(studentCardType, routeCardType, activation)
            }

            fun createMunicipalCard(
                municipalCardType: MunicipalCardType,
                routeCardType: RouteCardType,
                activation: CardActivatedState
            ): Flag.Municipal {
                return Flag.Municipal(municipalCardType, routeCardType, activation)
            }

        }

    }

    companion object {

        val DEFAULT = Card(Long.MAX_VALUE, Flag.createClassicCard(ClassicCardType.ADULT, RouteCardType.LOCAL_AND_DISTRICT, CardActivatedState.ACTIVATED), 200, Time.getAddYearToCurrentTimeSeconds())

        fun fromBytes(block4Id: Long, cardInfo: ByteArray): Card {
            val card_info = cardInfo.toLong()
            val unk_flags = card_info and 0xFF;

            val amount = (card_info shr 8) and 0xFFFF;
            val expiry_incorrect = ((card_info shr 24) and 0xFFFF_FFFF_FF);
            val expiry_correct = expiry_incorrect;

            val cardType: Int = (unk_flags and 0b11).toInt();
            val type_of_type: Int = ((unk_flags shr 2) and 0b11).toInt();
            val route_type_bin: Int = ((unk_flags shr 4) and 0b11).toInt();
            val activation_state_bin: Int = ((unk_flags shr 6) and 0b11).toInt();


            var classic_type: ClassicCardType? = null;
            var subscription_type: SubscriptionCardType? = null;
            var student_type: StudentCardType? = null;
            var municipal_type: MunicipalCardType? = null;
            var route_type = RouteCardType.entries.toTypedArray()[route_type_bin];
            val activation_state = CardActivatedState.entries.toTypedArray()[activation_state_bin];

            val flag = when {
                CardType.entries[cardType] == CardType.CLASSIC -> {
                    classic_type = ClassicCardType.entries.toTypedArray()[type_of_type];
                    Flag.createClassicCard(classic_type, route_type, activation_state)
                }

                CardType.entries.toTypedArray()[cardType] == CardType.SUBSCRIPTION -> {
                    subscription_type = SubscriptionCardType.entries.toTypedArray()[type_of_type];
                    Flag.createSubscriptionCard(subscription_type, route_type, activation_state)

                }

                CardType.entries.toTypedArray()[cardType] == CardType.STUDENT -> {
                    student_type = StudentCardType.entries.toTypedArray()[type_of_type]
                    Flag.createStudentCard(student_type, route_type, activation_state)
                }

                CardType.entries.toTypedArray()[cardType] == CardType.MUNICIPAL -> {
                    municipal_type = MunicipalCardType.entries.toTypedArray()[type_of_type]
                    Flag.createMunicipalCard(municipal_type, route_type, activation_state)
                }

                else -> throw Exception("Invalid card type")
            }
            return Card(block4Id, flag, amount.toShort(), expiry_correct)
        }
    }
}

