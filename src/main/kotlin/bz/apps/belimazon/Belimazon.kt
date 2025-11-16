package bz.apps.belimazon

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.onClick
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import bz.Config
import bz.TESTING
import bz.apps.ComposeApp
import bz.apps.belimazon.services.AssignedDeliveryService
import bz.apps.belimazon.services.MockAssignedDeliveryService
import bz.apps.belimazon.usecase.GetDeliveriesUseCase
import bz.apps.belimazon.viewmodels.MyRouteViewModel
import bz.state
import bz.ui.CameraViewWithScanner
import bz.ui.CameraViewWithScanner2
import bz.ui.SelectionButton
import bz.ui.const.Theme
import com.google.zxing.BarcodeFormat
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Belimazon: ComposeApp {

    private val testing get() = TESTING

    private lateinit var retrofit: Retrofit

    private val assignedDeliveryService by lazy {
        if(testing) {
            println("Testing mode enabled")
            mockAssignedDeliverySevice()
        } else {
            println("Testing mode disabled")
            createAssignedDeliveryService()
        }
    }
    private val vm = MyRouteViewModel(assignedDeliveryService)

    override fun run(): @Composable ApplicationScope.() -> Unit = {

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
                    Routes.MyRoute -> HeaderScreen(onBack = {
                        Router.push(Routes.Home)
                    }) {
                        MyRouteScreen()
                    }

                    Routes.Configuration -> HeaderScreen(onBack = {
                        Router.push(Routes.Home)
                    }) {
                        Text("Configuration Screen")
                    }

                    Routes.ScanProduct.Home, Routes.ScanProduct.DropOff, Routes.ScanProduct.Pickup -> {
                        ScanProductScreen()
                    }

                    Routes.Search -> HeaderScreen(onBack = {
                        Router.push(Routes.Home)
                    }) {
                        Text("Search Screen")
                    }
                }
            }

        }
    }

    @Preview
    @Composable
    @OptIn(ExperimentalFoundationApi::class)
    fun HeaderScreen(onBack: (() -> Unit)? = null, content: @Composable (() -> Unit)? = null) {
        val painter = painterResource("delete.png")
        Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
            Icon(
                painter = painter,
                modifier = Modifier.size(48.dp).onClick {
                    onBack?.invoke()
                }, contentDescription = ""
            )
            content?.invoke()
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
                        when (route) {
                            Routes.Configuration -> Unit
                            Routes.Home -> Unit
                            Routes.MyRoute -> vm.getDeliveries()
                            Routes.ScanProduct.Home -> Unit
                            Routes.Search -> Unit
                            else -> Unit
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
        when (val data = deliveries.value) {
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

    @Preview
    @Composable
    fun ScanProductScreen() {
        val path = Router.path.collectAsState()
        when(path.value) {
            Routes.ScanProduct.Home -> {
                HeaderScreen(onBack = {
                    Router.push(Routes.Home)
                }) {
                    Text("Select an Option")
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
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
                                    "Pick-Up",
                                    modifier,
                                    colors = Theme.Button
                                ) {
                                    Router.push(Routes.ScanProduct.Pickup)
                                }
                            }
                        }
                        item {
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
                                    "Drop-Off",
                                    modifier,
                                    colors = Theme.Button
                                ) {
                                    Router.push(Routes.ScanProduct.DropOff)
                                }
                            }
                        }
                    }
                }
            }
            Routes.ScanProduct.Pickup -> {
                HeaderScreen(onBack = {
                    Router.push(Routes.ScanProduct.Home)
                }) {
                    Text("Scan Product")
                    CameraViewWithScanner(Modifier.fillMaxSize(), 1) { code, barcode: BarcodeFormat ->
                        val formatted = code.chunked(4).joinToString("-")
                        println("Barcode: ($code)")
                        println("Formatted Barcode: ($formatted)")
                        println("Barcode Format: $barcode")
                        if(path.value == Routes.ScanProduct.Pickup) {
                            vm.scanOutForDelivery(formatted)
                        } else if(path.value == Routes.ScanProduct.DropOff) {
                            vm.scanDelivered(formatted)
                        }
                    }
                }
            }Routes.ScanProduct.DropOff -> {
                HeaderScreen(onBack = {
                    Router.push(Routes.ScanProduct.Home)
                }) {
                    Text("Scan Product")
                    CameraViewWithScanner2(Modifier.fillMaxSize(), 1) { code, barcode: BarcodeFormat ->
                        val formatted = code.chunked(4).joinToString("-")
                        println("Barcode: ($code)")
                        println("Formatted Barcode: ($formatted)")
                        println("Barcode Format: $barcode")
                        if(path.value == Routes.ScanProduct.Pickup) {
                            vm.scanOutForDelivery(formatted)
                        } else if(path.value == Routes.ScanProduct.DropOff) {
                            vm.scanDelivered(formatted)
                        }
                    }
                }
            }
            else -> Unit
        }
    }

    private fun createAssignedDeliveryService(): AssignedDeliveryService {
        return createRetrofitClient().create(AssignedDeliveryService::class.java)
    }

    private fun mockAssignedDeliverySevice(): AssignedDeliveryService {
        return MockAssignedDeliveryService()
    }


    private fun createRetrofitClient(): Retrofit {
        val build = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor(System.err::println).setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
        retrofit = Retrofit.Builder()
            .client(build)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(Config.getString("BASE_URL")?:throw Exception("Base URL not defined"))
            .build()
        return retrofit
    }

}