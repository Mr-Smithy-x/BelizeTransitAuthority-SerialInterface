package bz.apps.belimazon.usecase

import bz.apps.belimazon.models.ShippingLabel
import bz.apps.belimazon.services.AssignedDeliveryService
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetDeliveriesUseCase(
    val service: AssignedDeliveryService
) {

    sealed class GetDeliveriesState {
        data class Success(val data: List<ShippingLabel.OutForDelivery>) : GetDeliveriesState()
        data class Error(val message: String) : GetDeliveriesState()
        data object Loading: GetDeliveriesState()
    }

    operator fun invoke() = flow {
        emit(GetDeliveriesState.Loading)
        val response = service.getDeliveries()
        if (response.isSuccessful) {
            val value = response.body()?.data
            if (value == null) {
                emit(GetDeliveriesState.Error("No data found"))
            } else {
                emit(GetDeliveriesState.Success(value))
            }
        } else {
            emit(GetDeliveriesState.Error("Failed to scan"))
        }
    }.catch {
        emit(GetDeliveriesState.Error(it.message ?: "Unknown error"))
    }

}

