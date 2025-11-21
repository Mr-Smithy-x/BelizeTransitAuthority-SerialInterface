package bz.tracker.usecase.impl

import bz.apps.busfare.rw.io.Serial
import bz.apps.busfare.rw.models.Response
import bz.tracker.usecase.GPSUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GPSUseCaseImpl(private val serial: Serial) : GPSUseCase {

    sealed class GPSSerialState {

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
            val failedCheckSum: String
        ) : GPSSerialState()

        data class Success(
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
            val failedCheckSum: String
        ) : GPSSerialState()

        data class Message(val message: String) : GPSSerialState()
        data class Error(val exception: Exception) : GPSSerialState()
        data object NoPosition : GPSSerialState()

    }

    // output 3
    override operator fun invoke(): Flow<Response<GPSSerialState>> = flow {
        emit(Response.Loading)
        while (serial.isOpened) {
            val readLine = serial.readLine()
            if (readLine != null) {
                println("GPS: $readLine")
                val res = readLine.substring(0, 3)
                val substring = readLine.substring(3)
                when (res) {
                    "[S]", "[U]" -> {
                        val response = substring.split("|").map {
                            val split = it.split(":")
                            split[1]
                        }
                        val gps = if (res == "[U]") {
                            GPSSerialState.Updated(
                                latitude = response[0],
                                longitude = response[1],
                                speed = response[2],
                                course = response[3],
                                courseCardinal = response[4],
                                satellites = response[5],
                                hdop = response[6],
                                altitude = response[7],
                                datetime = response[8],
                                age = response[9],
                                charactersProcessed = response[10],
                                sentencesFixed = response[11],
                                failedCheckSum = response[12]
                            )
                        } else {
                            GPSSerialState.Success(
                                latitude = response[0],
                                longitude = response[1],
                                speed = response[2],
                                course = response[3],
                                courseCardinal = response[4],
                                satellites = response[5],
                                hdop = response[6],
                                altitude = response[7],
                                datetime = response[8],
                                age = response[9],
                                charactersProcessed = response[10],
                                sentencesFixed = response[11],
                                failedCheckSum = response[12]
                            )
                        }
                        emit(Response.WritingToSerial(gps, substring))
                    }
                    "[I]" -> emit(Response.WritingToSerial(GPSSerialState.Message(substring), substring))
                    "[E]" -> emit(Response.Error(Exception(substring)))
                    else -> emit(Response.Error(Exception("Invalid response")))
                }
            } else {
                emit(Response.Error(Exception("Failed to read from serial port")))
            }
        }
    }.catch {
        emit(Response.Error(Exception(it.message ?: "Unknown error")))
    }

}
