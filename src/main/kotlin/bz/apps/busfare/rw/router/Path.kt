package bz.apps.busfare.rw.router

sealed interface Path {
    val name: String

    data object HOME : Path {
        override val name: String = "HOME"
    }

    data object ROUTES : Path {
        override val name: String = "BUS ROUTE"
    }

    data object STATS : Path {
        override val name: String = "STATISTICS"
    }

    data object DIAGNOSTIC : Path {
        override val name: String = "DIAGNOSTICS"
    }

    data object SETTINGS : Path {
        override val name: String = "SETTINGS"
    }

}