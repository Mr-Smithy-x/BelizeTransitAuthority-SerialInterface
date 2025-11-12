package bz.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

open class ViewModel: CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val job get() = SupervisorJob()
}
