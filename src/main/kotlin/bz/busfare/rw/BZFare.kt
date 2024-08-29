package bz.busfare.rw

import bz.busfare.rw.network.CardService
import bz.busfare.rw.viewmodel.state.CardState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.CoroutineContext

object BZFare: CoroutineScope {

    private lateinit var cardService: CardService
    private lateinit var retrofit: Retrofit
    private val job = Job()
    var lastJob: Job? = null

    val reader: UnoMifareReader by lazy {
        UnoMifareReader("COM4")
    }


    fun openAndRead(opened: (Boolean) -> Unit): Job {
        if (lastJob != null && lastJob?.isActive != false) {
            reader.close()
            job.cancel()
        }
        reader.open(opened)
        return CoroutineScope(Dispatchers.IO + job).launch {

            reader.read { cardState ->
                when(cardState) {
                    CardState.Default -> Unit
                    is CardState.Error -> {

                    }
                    is CardState.Initialized -> {

                    }
                    is CardState.Loading -> Unit
                    is CardState.Updated -> {

                    }
                }
            }
        }
    }


    private fun createRetrofitClient(): Retrofit {
        if (!::retrofit.isInitialized) {
            val build = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor(System.err::println).setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
            retrofit = Retrofit.Builder()
                .client(build)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://localhost:8000")
                .build()
        }
        return retrofit
    }

    fun getCardService(): CardService {
        if (!this::cardService.isInitialized) {
            cardService = createRetrofitClient().create(CardService::class.java)
        }
        return cardService
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

}