package bz.apps.busfare.rw.models

sealed class Response<out T> {
    data object Loading : Response<Nothing>()
    data class WritingToSerial<T>(val data: T, val serialData: String) : Response<T>()
    data class NetworkSuccess<T>(val data: T) : Response<T>()
    data class Error(val exception: Exception) : Response<Nothing>()
    data class CloseSerial<T>(val data: T) : Response<T>()
}


