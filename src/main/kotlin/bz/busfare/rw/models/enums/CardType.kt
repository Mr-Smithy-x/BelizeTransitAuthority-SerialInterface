package bz.busfare.rw.models.enums

/**
 * Enum for cards, this should all take one byte.
 */
enum class CardType {
    CLASSIC,        // 0b00
    SUBSCRIPTION,   // 0b01
    STUDENT,        // 0b10
    MUNICIPAL          // 0b11
}