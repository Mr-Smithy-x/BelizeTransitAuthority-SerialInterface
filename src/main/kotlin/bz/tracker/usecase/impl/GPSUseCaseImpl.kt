package bz.tracker.usecase.impl

import bz.apps.busfare.rw.io.Serial
import bz.apps.busfare.rw.models.Response
import bz.tracker.usecase.GPSUseCase
import bz.tracker.usecase.state.GPSSerialState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GPSUseCaseImpl(private val serial: Serial) : GPSUseCase {

    // output 3
    override operator fun invoke(): Flow<Response<GPSSerialState>> = flow {
        emit(Response.Loading)
        while (serial.isOpened) {
            val readLine = serial.readLine()
            if (readLine != null) {
                try {
                    val res = readLine.substring(0, 3)
                    val substring = readLine.substring(3)
                    when (res) {
                        "[S]", "[U]" -> {
                            val response = substring.split("|").map {
                                val split = it.split(":", limit = 2)
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
                                    failedCheckSum = response[12],
                                    substring
                                )
                            } else {
                                GPSSerialState.Positioning(
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
                                    failedCheckSum = response[12],
                                    substring
                                )
                            }
                            emit(Response.WritingToSerial(gps, substring))
                        }

                        "[I]" -> emit(Response.WritingToSerial(GPSSerialState.Message(substring), substring))
                        "[E]" -> emit(Response.Error(Exception(substring)))
                        else -> emit(Response.Error(Exception("Invalid response")))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    emit(Response.Error(Exception(e.message ?: "Unknown error")))
                }
            } else {
                emit(Response.Error(Exception("Failed to read from serial port")))
            }
        }
    }.catch {
        it.printStackTrace()
        emit(Response.Error(Exception(it.message ?: "Unknown error")))
    }

}
