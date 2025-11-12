package bz.apps.belimazon

sealed class Routes {
    data object Home : Routes()
    data object MyRoute : Routes()
    sealed class ScanProduct: Routes() {
        data object Home: ScanProduct()
        data object Pickup : ScanProduct()
        data object DropOff : ScanProduct()
    }
    data object Search : Routes()
    data object Configuration : Routes()
}