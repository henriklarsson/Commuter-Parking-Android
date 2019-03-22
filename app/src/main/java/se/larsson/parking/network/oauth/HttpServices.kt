package se.larsson.parking.network.oauth

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HttpServices {
    fun getOAuthService(): OAuthService{
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.vasttrafik.se:443")
            .client(getOkHttpClient())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(OAuthService::class.java)
    }

    private fun getOkHttpClient() = OkHttpClient()

}