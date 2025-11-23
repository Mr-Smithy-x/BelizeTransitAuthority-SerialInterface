package bz.apps.tracker.viewmodel


import bz.apps.busfare.rw.io.Serial
import bz.apps.busfare.rw.io.impl.SerialImpl
import bz.apps.busfare.rw.models.Response
import bz.base.ViewModel
import bz.apps.tracker.usecase.GPSUseCase
import bz.apps.tracker.usecase.impl.GPSUseCaseImpl
import bz.apps.tracker.usecase.state.GPSSerialState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GPSViewModel(
    private val port: String,
    internal val serial: Serial = SerialImpl(port, 115200),
    private val useCase: GPSUseCase = GPSUseCaseImpl(serial),
) : ViewModel() {

    private val _state: MutableStateFlow<GPSSerialState> = MutableStateFlow(GPSSerialState.NoPosition)
    val state: StateFlow<GPSSerialState> get() = _state

    fun run() = launch {
        useCase.invoke().collect {
            when(it) {
                is Response.WritingToSerial<GPSSerialState> -> {
                    _state.emit(it.data)
                }
                is Response.Error -> {
                    if(_state.value !is GPSSerialState.Positioning && _state.value !is GPSSerialState.Updated) {
                        _state.emit(GPSSerialState.Error(it.exception))
                    }
                }
                Response.Loading -> {
                    if(_state.value !is GPSSerialState.Positioning && _state.value !is GPSSerialState.Updated) {
                        _state.emit(GPSSerialState.NoPosition)
                    }
                }
                else -> Unit
            }
        }
    }



}
