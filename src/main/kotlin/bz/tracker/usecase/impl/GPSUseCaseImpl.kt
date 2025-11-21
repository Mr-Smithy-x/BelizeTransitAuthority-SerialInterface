package bz.tracker.usecase.impl

import bz.apps.busfare.rw.io.Serial
import bz.apps.busfare.rw.models.Response
import bz.tracker.usecase.GPSUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GPSUseCaseImpl(private val serial: Serial) : GPSUseCase {

    data class GPSSerial(
        val latitude: String,
        val longitude: String,
        val speed: String,
        val course: String
    )
    // output 3
    override operator fun invoke(): Flow<Response<GPSSerial>> = flow {
        emit(Response.Loading)
        while (serial.isOpened) {
            val readLine = serial.readLine()
            if(readLine != null) {
                val res = readLine.substring(0, 3)
                val substring = readLine.substring(3)
                when(res) {
                    "[S]" -> {
                        val response = substring.split("|").map {
                            val split = it.split(":")
                            split[1]
                        }
                        val gps = GPSSerial(response[0], response[1], response[2], response[3])
                        emit(Response.WritingToSerial(gps, substring))
                    }
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
