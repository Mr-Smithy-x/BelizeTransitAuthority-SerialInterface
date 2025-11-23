package bz.apps.tracker.usecase

import bz.apps.busfare.rw.models.Response
import bz.apps.tracker.usecase.state.GPSSerialState
import kotlinx.coroutines.flow.Flow

interface GPSUseCase {

    operator fun invoke(): Flow<Response<GPSSerialState>>
}
