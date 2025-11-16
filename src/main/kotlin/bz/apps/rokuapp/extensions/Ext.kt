package bz.apps.rokuapp.extensions

import java.net.InetAddress
import java.net.UnknownHostException

fun String.parseIpAndPort(): Pair<String, Int>? {
    var addressWithoutProtocol = if (startsWith("http://", true)) {
        substring(7) // Remove "http://" if present (case-insensitive)
    } else if (startsWith("https://", true)) {
        substring(8) // Remove "https://" if present (case-insensitive)
    } else {
        this
    }
    if(addressWithoutProtocol.endsWith("/")) {
        addressWithoutProtocol = addressWithoutProtocol.substring(0 until addressWithoutProtocol.length-1)
    }
    val parts = addressWithoutProtocol.split(":")
    println(parts)
    if (parts.size != 2) return null

    val ipAddress: String
    try {
        ipAddress = InetAddress.getByName(parts[0]).hostAddress ?: return null
    } catch (e: UnknownHostException) {
        return null
    }

    val port = try {
        parts[1].toInt()
    } catch (e: NumberFormatException) {
        return null
    }

    return Pair(ipAddress, port)
}
