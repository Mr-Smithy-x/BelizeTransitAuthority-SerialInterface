package bz.apps.belimazon.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import bz.apps.belimazon.services.AssignedDeliveryService
import bz.apps.belimazon.usecase.GetDeliveriesUseCase
import bz.apps.belimazon.usecase.ScanDeliveredUseCase
import bz.apps.belimazon.usecase.ScanOutForDeliveryUseCase
import bz.base.ViewModel
import kotlinx.coroutines.launch

class MyRouteViewModel(
    assignedDeliveryService: AssignedDeliveryService,
    private val getDeliveriesUseCase: GetDeliveriesUseCase = GetDeliveriesUseCase(assignedDeliveryService),
    private val scanOutForDeliveryUseCase: ScanOutForDeliveryUseCase = ScanOutForDeliveryUseCase(
        assignedDeliveryService
    ),
    private val scanDeliveredUseCase: ScanDeliveredUseCase = ScanDeliveredUseCase(assignedDeliveryService)
) : ViewModel() {

    private val _deliveries =
        mutableStateOf<GetDeliveriesUseCase.GetDeliveriesState>(GetDeliveriesUseCase.GetDeliveriesState.Loading)
    val deliveries: State<GetDeliveriesUseCase.GetDeliveriesState> get() = _deliveries

    fun scanOutForDelivery(shipping_id: String) = launch {
        scanOutForDeliveryUseCase(shipping_id).collect { state ->
            when (state) {
                is ScanOutForDeliveryUseCase.ScanState.Success -> {
                    println("Success")
                }

                is ScanOutForDeliveryUseCase.ScanState.Error -> {
                    println(state.message)
                }

                is ScanOutForDeliveryUseCase.ScanState.Scanning -> {
                    println("Scanning: $shipping_id")
                }
            }
        }
    }

    fun getDeliveries() = launch {
        getDeliveriesUseCase().collect { state ->
            _deliveries.value = state
            when (state) {
                is GetDeliveriesUseCase.GetDeliveriesState.Success -> {
                    println("Success")
                }
                is GetDeliveriesUseCase.GetDeliveriesState.Error -> {
                    println(state.message)
                }
                GetDeliveriesUseCase.GetDeliveriesState.Loading -> {
                    println("Loading")
                }
            }
        }
    }

    fun scanDelivered(shipping_id: String) = launch {
        scanDeliveredUseCase(shipping_id).collect { state ->
            when(state) {
                is ScanDeliveredUseCase.ScanDeliverState.Success -> {
                    println("Success")
                }
                is ScanDeliveredUseCase.ScanDeliverState.Error -> {
                    println(state.message)
                }
                is ScanDeliveredUseCase.ScanDeliverState.Scanning -> {
                    println("Scanning: $shipping_id")
                }
            }
        }
    }

}