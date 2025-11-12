package bz.apps.belimazon.usecase

import bz.apps.belimazon.models.ShippingLabel
import bz.apps.belimazon.services.AssignedDeliveryService
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class ScanDeliveredUseCase(
    val service: AssignedDeliveryService
) {

    sealed class ScanDeliverState {
        data class Success(val data: ShippingLabel.Delivered) : ScanDeliverState()
        data class Error(val message: String) : ScanDeliverState()
        data class Scanning(val shipping_id: String) : ScanDeliverState()
    }

    operator fun invoke(shipping_id: String) = flow {
        emit(ScanDeliverState.Scanning(shipping_id))
        val response = service.delivered(shipping_id)
        if (response.isSuccessful) {
            val value = response.body()?.data
            if (value == null) {
                emit(ScanDeliverState.Error("No data found"))
            } else {
                emit(ScanDeliverState.Success(value))
            }
        } else {
            emit(ScanDeliverState.Error("Failed to scan"))
        }
    }.catch {
        emit(ScanDeliverState.Error(it.message ?: "Unknown error"))
    }

}

