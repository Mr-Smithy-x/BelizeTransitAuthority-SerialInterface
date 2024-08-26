package bz.busfare.rw

import bz.busfare.rw.network.CardService
import bz.busfare.rw.usecase.CardUseCase
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object BZFare {

    private lateinit var cardService: CardService
    private lateinit var retrofit: Retrofit


    private fun createRetrofitClient(): Retrofit {
        if (!::retrofit.isInitialized) {
            val build = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
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

}