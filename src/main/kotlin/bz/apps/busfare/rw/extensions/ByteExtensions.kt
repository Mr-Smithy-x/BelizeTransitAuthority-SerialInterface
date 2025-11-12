package bz.apps.busfare.rw.extensions

import java.nio.ByteBuffer


fun ULong.toByteArray(): ByteArray {
    val result = ByteArray(ULong.SIZE_BYTES)
    (0 until ULong.SIZE_BYTES).forEach {
        result[it] = this.shr(Byte.SIZE_BITS * it).toByte()
    }
    return result
}

fun Long.toByteArray(): ByteArray {
    val result = ByteArray(ULong.SIZE_BYTES)
    (0 until Long.SIZE_BYTES).forEach {
        result[it] = this.shr(Byte.SIZE_BITS * it).toByte()
    }
    return result
}

@OptIn(ExperimentalStdlibApi::class)
fun ByteArray.toLong(): Long {
    val reversed = this.reversedArray()
    val buffer = ByteBuffer.wrap(reversed)
    return buffer.asLongBuffer().get()
}