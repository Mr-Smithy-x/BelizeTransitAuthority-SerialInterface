package bz.tracker.usecase

import bz.apps.busfare.rw.models.Response
import bz.tracker.usecase.state.GPSSerialState
import kotlinx.coroutines.flow.Flow

interface GPSUseCase {

    operator fun invoke(): Flow<Response<GPSSerialState>>
}
