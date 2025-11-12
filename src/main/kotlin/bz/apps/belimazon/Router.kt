package bz.apps.belimazon

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object Router {
    val homeRoutes = listOf(Routes.ScanProduct.Home, Routes.MyRoute, Routes.Search, Routes.Configuration)
    private val _path = MutableStateFlow<Routes>(Routes.Home)
    val path: StateFlow<Routes> get() = _path

    fun push(route: Routes) {
        _path.value = route
    }


}