package bz.apps.busfare.rw.models

data class Data(val hex: String, val byteArray: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Data

        if (hex != other.hex) return false
        if (!byteArray.contentEquals(other.byteArray)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = hex.hashCode()
        result = 31 * result + byteArray.contentHashCode()
        return result
    }
}