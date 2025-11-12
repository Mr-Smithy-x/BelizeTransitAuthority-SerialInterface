package bz.apps.belimazon.usecase

import bz.apps.belimazon.models.ShippingLabel
import bz.apps.belimazon.services.AssignedDeliveryService
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class ScanOutForDeliveryUseCase(
    val service: AssignedDeliveryService
) {

    sealed class ScanState {
        data class Success(val data: ShippingLabel.OutForDelivery) : ScanState()
        data class Error(val message: String) : ScanState()
        data class Scanning(val shipping_id: String) : ScanState()
    }

    operator fun invoke(shipping_id: String) = flow {
        emit(ScanState.Scanning(shipping_id))
        val response = service.scanToOutForDelivery(shipping_id)
        if (response.isSuccessful) {
            val value = response.body()?.data
            if (value == null) {
                emit(ScanState.Error("No data found"))
            } else {
                emit(ScanState.Success(value))
            }
        } else {
            emit(ScanState.Error("Failed to scan"))
        }
    }.catch {
        emit(ScanState.Error(it.message ?: "Unknown error"))
    }

}

