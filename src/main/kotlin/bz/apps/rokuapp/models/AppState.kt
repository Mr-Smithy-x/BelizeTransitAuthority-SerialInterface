package bz.apps.rokuapp.models

sealed class AppState {
    data object None: AppState()
    data class Apps(val apps: List<Application>): AppState() {
        val inputs get() = apps.filter { it.type == "tvin" }
        val tvapps get() = apps.filter { it.type != "tvin" }
    }
    data class Active(val app: Application): AppState()
}