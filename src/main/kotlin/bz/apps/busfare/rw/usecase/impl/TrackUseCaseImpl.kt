package bz.apps.busfare.rw.usecase.impl

import bz.Config
import bz.apps.busfare.rw.models.Response
import bz.apps.busfare.rw.models.network.BusTracker
import bz.apps.busfare.rw.network.TrackerService
import bz.apps.busfare.rw.usecase.TrackUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackUseCaseImpl(private val trackService: TrackerService) : TrackUseCase {
    override fun updateLocation(): Flow<Response<BusTracker>> = flow {
        emit(Response.Loading)
        val bus_id = Config.getString("BUS_ID") ?: return@flow
        val route = Config.getString("ROUTE")?: return@flow
        val lat = 0.0
        val lon = 0.0
        val updateLocation = trackService.updateLocation(bus_id, route, lat, lon)
        if(updateLocation.isSuccessful) {
            emit(Response.NetworkSuccess(updateLocation.body()?.data!!))
        } else {
            emit(Response.Error(Exception("Failed to update location")))
        }
    }

}
