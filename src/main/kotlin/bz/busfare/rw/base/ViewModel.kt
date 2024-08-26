package bz.busfare.rw.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

open class ViewModel: CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val job = Job()
}
