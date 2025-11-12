package bz.apps.belimazon

sealed class Routes {
    data object Home : Routes()
    data object MyRoute : Routes()
    data object ScanProduct: Routes()
    data object Search : Routes()
    data object Configuration : Routes()
}