package bz.apps.tracker.usecase.state

sealed class GPSSerialState(open val raw: String) {

    data class Updated(
        val latitude: String,
        val longitude: String,
        val speed: String,
        val course: String,
        val courseCardinal: String,
        val satellites: String,
        val hdop: String,
        val altitude: String,
        val datetime: String,
        val age: String,
        val charactersProcessed: String,
        val sentencesFixed: String,
        val failedCheckSum: String,
        override val raw: String
    ) : GPSSerialState(raw)

    data class Positioning(
        val latitude: String,
        val longitude: String,
        val speed: String,
        val course: String,
        val courseCardinal: String,
        val satellites: String,
        val hdop: String,
        val altitude: String,
        val datetime: String,
        val age: String,
        val charactersProcessed: String,
        val sentencesFixed: String,
        val failedCheckSum: String,
        override val raw: String
    ) : GPSSerialState(raw)

    data class Message(val message: String) : GPSSerialState(message)
    data class Error(val exception: Exception) : GPSSerialState(exception.message ?: "Unknown Error")
    data object NoPosition : GPSSerialState("No position")

}