package bz.busfare.rw.usecase

import bz.busfare.rw.models.Response
import bz.busfare.rw.models.network.BusTracker
import kotlinx.coroutines.flow.Flow

interface TrackUseCase {

    fun updateLocation(): Flow<Response<BusTracker>>

}
