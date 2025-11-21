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


    private var job: Job? = null
    private val _state: MutableStateFlow<GPSUseCaseImpl.GPSSerialState> = MutableStateFlow(GPSUseCaseImpl.GPSSerialState.NoPosition)
    val state: StateFlow<GPSUseCaseImpl.GPSSerialState> get() = _state

    fun run() = launch {
        useCase.invoke().collect {
            when(it) {
                is Response.WritingToSerial<GPSUseCaseImpl.GPSSerialState> -> {
                    _state.emit(it.data)
                }
                is Response.Error -> {
                    if(_state.value !is GPSUseCaseImpl.GPSSerialState.Success && _state.value !is GPSUseCaseImpl.GPSSerialState.Updated) {
                        _state.emit(GPSUseCaseImpl.GPSSerialState.Error(it.exception))
                    }
                }
                Response.Loading -> {
                    if(_state.value !is GPSUseCaseImpl.GPSSerialState.Success && _state.value !is GPSUseCaseImpl.GPSSerialState.Updated) {
                        _state.emit(GPSUseCaseImpl.GPSSerialState.NoPosition)
                    }
                }
                else -> Unit
            }
        }
    }



}
