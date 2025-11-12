package bz.apps.busfare.rw.usecase

import bz.apps.busfare.rw.models.Response
import bz.apps.busfare.rw.models.network.BusTracker
import kotlinx.coroutines.flow.Flow

interface TrackUseCase {

    fun updateLocation(): Flow<Response<BusTracker>>

}
