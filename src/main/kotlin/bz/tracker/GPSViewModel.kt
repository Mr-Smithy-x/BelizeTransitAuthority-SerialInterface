package bz.tracker


import bz.apps.busfare.rw.BZFare
import bz.base.ViewModel
import bz.apps.busfare.rw.io.Serial
import bz.apps.busfare.rw.io.impl.SerialImpl
import bz.apps.busfare.rw.models.Data
import bz.apps.busfare.rw.models.Response
import bz.apps.busfare.rw.usecase.CardUseCase
import bz.apps.busfare.rw.usecase.TrackUseCase
import bz.apps.busfare.rw.usecase.impl.CardUseCaseImpl
import bz.apps.busfare.rw.usecase.impl.TrackUseCaseImpl
import bz.apps.busfare.rw.viewmodel.state.CardState
import bz.tracker.usecase.GPSUseCase
import bz.tracker.usecase.impl.GPSUseCaseImpl
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GPSViewModel(
    private val port: String,
    internal val serial: Serial = SerialImpl(port),
    private val useCase: GPSUseCase = GPSUseCaseImpl(serial),
) : ViewModel() {

    sealed class GPSState {
        data object NoPosition : GPSState()
        data class Success(val data: Data) : GPSState()
        data class Error(val message: String) : GPSState()
    }

    private var job: Job? = null
    private val _state: MutableStateFlow<GPSState> = MutableStateFlow(GPSState.NoPosition)
    val state: StateFlow<GPSState> get() = _state

    fun run() = launch {
        useCase.invoke().collect {
            when(it) {
                is Response.WritingToSerial<GPSUseCaseImpl.GPSSerial> -> {

                }
                is Response.Error -> {

                }
                Response.Loading -> {

                }
                else -> Unit
            }
        }
    }

}
