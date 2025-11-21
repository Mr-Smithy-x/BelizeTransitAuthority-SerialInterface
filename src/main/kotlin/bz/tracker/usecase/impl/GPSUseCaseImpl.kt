package bz.tracker.usecase.impl

import bz.apps.busfare.rw.io.Serial
import bz.apps.busfare.rw.models.Response
import bz.tracker.usecase.GPSUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GPSUseCaseImpl(private val serial: Serial) : GPSUseCase {

    sealed class GPSSerial {

        data class Updated(
            override val latitude: String,
            override val longitude: String,
            override val speed: String,
            override val course: String,
            override val courseCardinal: String,
            override val satellites: String,
            override val hdop: String,
            override val altitude: String,
            override val datetime: String,
            override val age: String,
            override val charactersProcessed: String,
            override val sentencesFixed: String,
            override val failedCheckSum: String
        ) : Success (
            latitude,
            longitude,
            speed,
            course,
            courseCardinal,
            satellites,
            hdop,
            altitude,
            datetime,
            age,
            charactersProcessed,
            sentencesFixed,
            failedCheckSum
        )

        open class Success(
            open val latitude: String,
            open val longitude: String,
            open val speed: String,
            open val course: String,
            open val courseCardinal: String,
            open val satellites: String,
            open val hdop: String,
            open val altitude: String,
            open val datetime: String,
            open val age: String,
            open val charactersProcessed: String,
            open val sentencesFixed: String,
            open val failedCheckSum: String
        ) : GPSSerial()

        data class Message(val message: String) : GPSSerial()
        data class Error(val exception: Exception) : GPSSerial()

    }

    // output 3
    override operator fun invoke(): Flow<Response<GPSSerial>> = flow {
        emit(Response.Loading)
        while (serial.isOpened) {
            val readLine = serial.readLine()
            if (readLine != null) {
                val res = readLine.substring(0, 3)
                val substring = readLine.substring(3)
                when (res) {
                    "[S]", "[U]" -> {
                        val response = substring.split("|").map {
                            val split = it.split(":")
                            split[1]
                        }
                        val gps = if (res == "[U]") {
                            GPSSerial.Updated(
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
                            GPSSerial.Success(
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
                    "[I]" -> emit(Response.WritingToSerial(GPSSerial.Message(substring), substring))
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
