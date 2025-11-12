package bz.apps.busfare.rw.models.network

data class BusTracker(
    val id: Long,
    val bus_id: String,
    val route: String,
    val company: String,
    val latitude: Double,
    val longitude: Double,
    val created_at: String,
    val updated_at: String?,
)