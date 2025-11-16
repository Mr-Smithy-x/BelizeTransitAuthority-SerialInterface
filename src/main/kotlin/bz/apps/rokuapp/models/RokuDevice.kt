package bz.apps.rokuapp.models

sealed class Device {
    data object None: Device()
    data class RokuDevice(val ip: String, val port: Int) : Device()

    val url get(): String? {
        return when(this) {
            None -> null
            is RokuDevice -> "http://$ip:$port"
        }
    }
}
