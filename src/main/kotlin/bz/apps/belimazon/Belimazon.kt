package bz.apps.belimazon

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import bz.Config
import bz.apps.belimazon.services.AssignedDeliveryService
import bz.apps.belimazon.services.MockAssignedDeliveryService
import bz.apps.belimazon.usecase.GetDeliveriesUseCase
import bz.apps.belimazon.usecase.ScanDeliveredUseCase
import bz.apps.belimazon.usecase.ScanOutForDeliveryUseCase
import bz.base.ViewModel
import bz.state
import bz.ui.SelectionButton
import bz.ui.const.Theme
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Belimazon {

    private lateinit var retrofit: Retrofit
    private var assignedDeliveryService = mockAssignedDeliverySevice()

    private fun createAssignedDeliveryService(): AssignedDeliveryService {
        return createRetrofitClient().create(AssignedDeliveryService::class.java)
    }

    private fun mockAssignedDeliverySevice(): AssignedDeliveryService {
        return MockAssignedDeliveryService()
    }

    private val vm = MyRouteViewModel()

    private fun createRetrofitClient(): Retrofit {
        val build = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor(System.err::println).setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
        retrofit = Retrofit.Builder()
            .client(build)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(Config.getString("BASE_URL"))
            .build()

        return retrofit
    }


    class MyRouteViewModel(
        private val getDeliveriesUseCase: GetDeliveriesUseCase = GetDeliveriesUseCase(assignedDeliveryService),
        private val scanOutForDeliveryUseCase: ScanOutForDeliveryUseCase = ScanOutForDeliveryUseCase(
            assignedDeliveryService
        ),
        private val scanDeliveredUseCase: ScanDeliveredUseCase = ScanDeliveredUseCase(assignedDeliveryService)
    ) : ViewModel() {

        private val _deliveries = mutableStateOf<GetDeliveriesUseCase.GetDeliveriesState>(GetDeliveriesUseCase.GetDeliveriesState.Loading)
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

    fun run(): @Composable ApplicationScope.() -> Unit = {
        Config.load(".env.properties")
        state = rememberWindowState(
            placement = WindowPlacement.Fullscreen,
            isMinimized = false,
            WindowPosition(Alignment.Center)
        )

        val path: State<Routes> = Router.path.collectAsState()

        Window(
            onCloseRequest = {
                /*if (reader.isOpened) {
                    isReaderOpened = !reader.close()
                }*/
                exitApplication()
            },
            resizable = false, undecorated = true,
            title = "Belimazon",
            state = state
        ) {
            MenuBar {
                Menu("File") {
                    Item("Open") {

                    }
                    Item("Bypass Screen") {

                    }
                    Item("Exit") {
                        exitApplication()
                    }
                }
            }
            MaterialTheme(colors = darkColors(background = Theme.Base.background)) {
                when (path.value) {
                    Routes.Home -> HomeScreen()
                    Routes.MyRoute -> MyRouteScreen()
                    Routes.Configuration -> {

                    }

                    Routes.ScanProduct -> {

                    }

                    Routes.Search -> {


                    }
                }
            }

        }
    }


    @Preview
    @Composable
    fun HomeScreen() {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize()
        ) {
            items(Router.homeRoutes) { route ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    val dp = 0.dp
                    val modifier = Modifier.fillMaxSize()
                        .heightIn(max = state.size.height / 2 - dp, min = state.size.height / 2 - dp)
                        .widthIn(max = state.size.width / 2 - dp, min = state.size.width / 2 - dp)
                        .padding(16.dp).fillMaxSize()
                    SelectionButton(
                        route.javaClass.simpleName ?: "",
                        modifier,
                        colors = Theme.Button
                    ) {
                        when(route) {
                            Routes.Configuration -> Unit
                            Routes.Home -> Unit
                            Routes.MyRoute -> vm.getDeliveries()
                            Routes.ScanProduct -> {
                                vm.scanOutForDelivery("0001-0000-000")
                                return@SelectionButton
                            }
                            Routes.Search -> Unit
                        }
                        Router.push(route)
                    }
                }
            }
        }
    }

    @Preview
    @Composable
    fun MyRouteScreen() {
        val deliveries = vm.deliveries
        when(val data = deliveries.value) {
            is GetDeliveriesUseCase.GetDeliveriesState.Error -> {
                Text(data.message)
            }
            GetDeliveriesUseCase.GetDeliveriesState.Loading -> {
                Text("Loading")
            }
            is GetDeliveriesUseCase.GetDeliveriesState.Success -> {
                LazyColumn {
                    items(data.data) {
                        Text(it.toString())
                    }
                }
            }
        }
    }

}