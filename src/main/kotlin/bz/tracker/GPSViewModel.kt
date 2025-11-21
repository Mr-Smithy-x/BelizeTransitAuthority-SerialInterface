package bz.tracker


import bz.apps.busfare.rw.io.Serial
import bz.apps.busfare.rw.io.impl.SerialImpl
import bz.apps.busfare.rw.models.Response
import bz.base.ViewModel
import bz.tracker.usecase.GPSUseCase
import bz.tracker.usecase.impl.GPSUseCaseImpl
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GPSViewModel(
    private val port: String,
    internal val serial: Serial = SerialImpl(port, 115200),
    private val useCase: GPSUseCase = GPSUseCaseImpl(serial),
) : ViewModel() {

    sealed class GPSState {
        data object NoPosition : GPSState()
        data class Success(val data: GPSUseCaseImpl.GPSSerial) : GPSState()
        data class Error(val message: String) : GPSState()
    }

    private var job: Job? = null
    private val _state: MutableStateFlow<GPSState> = MutableStateFlow(GPSState.NoPosition)
    val state: StateFlow<GPSState> get() = _state

    fun run() = launch {
        useCase.invoke().collect {
            when(it) {
                is Response.WritingToSerial<GPSUseCaseImpl.GPSSerial> -> {
                    _state.emit(GPSState.Success(it.data))
                }
                is Response.Error -> {
                    if(_state.value !is GPSState.Success) {
                        _state.emit(GPSState.Error(it.exception.message ?: "Unknown error"))
                    }
                }
                Response.Loading -> {
                    if(_state.value !is GPSState.Success) {
                        _state.emit(GPSState.NoPosition)
                    }
                }
                else -> Unit
            }
        }
    }

}
