package bz.tracker.usecase

import bz.apps.busfare.rw.models.Response
import bz.tracker.usecase.impl.GPSUseCaseImpl
import kotlinx.coroutines.flow.Flow

interface GPSUseCase {

    operator fun invoke(): Flow<Response<GPSUseCaseImpl.GPSSerialState>>
}
