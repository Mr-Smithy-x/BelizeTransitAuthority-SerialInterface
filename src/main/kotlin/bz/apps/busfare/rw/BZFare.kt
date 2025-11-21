package bz.apps.busfare.rw

import bz.Config
import bz.apps.busfare.rw.Stats.saveLastScannedCard
import bz.apps.busfare.rw.network.CardService
import bz.apps.busfare.rw.network.TrackerService
import bz.apps.busfare.rw.viewmodel.state.CardState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.CoroutineContext

object BZFare : CoroutineScope {

    private lateinit var trackerService: TrackerService
    private lateinit var cardService: CardService
    private lateinit var retrofit: Retrofit
    private val job = Job()
    var lastJob: Job? = null

    val reader: UnoMifareReader by lazy {
        val string = Config.getString("PORT")
        println(string?:"No port defined in config")
        UnoMifareReader(string!!)
    }


    fun openAndRead(opened: (Boolean) -> Unit): Job {
        if (lastJob != null && lastJob?.isActive != false) {
            reader.close()
            job.cancel()
        }
        reader.init()
        reader.open(opened)
        return CoroutineScope(Dispatchers.IO + job).launch {
            reader.read { cardState ->
                when (cardState) {
                    CardState.Default -> Unit
                    is CardState.Loading -> Unit
                    is CardState.Error, is CardState.Initialized, is CardState.Updated -> saveLastScannedCard(cardState)
                }
            }
        }
    }



    private fun createRetrofitClient(): Retrofit {
        if (!BZFare::retrofit.isInitialized) {
            val build = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor(System.err::println).setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
            retrofit = Retrofit.Builder()
                .client(build)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Config.getString("BASE_URL"))
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

    fun getTrackService(): TrackerService {
        if (!this::cardService.isInitialized) {
            trackerService = createRetrofitClient().create(TrackerService::class.java)
        }
        return trackerService
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

}