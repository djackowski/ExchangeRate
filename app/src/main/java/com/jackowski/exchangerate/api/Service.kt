package com.jackowski.exchangerate.api

import android.content.Context
import com.google.gson.GsonBuilder
import com.jackowski.exchangerate.models.Rates
import com.jackowski.exchangerate.utils.Constants.DOMAIN
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

//API KEY c0e04631e68cd066ee0682d9f5041d26
/*http://data.fixer.io/api/YYYY-MM-DD
? access_key = YOUR_ACCESS_KEY
&symbols = USD,AUD,CAD,PLN,MXN*/

const val CONNECT_TIMEOUT = 100L
const val READ_TIMEOUT = 100L

interface Service {

    @GET("{date}")
    fun getRatesFromDate(@Path("date") date: String): Observable<Rates>

    @GET("latest")
    fun getLatestRates(): Observable<Rates>

    companion object {
        private fun createGsonConverter(): Converter.Factory {
            val gsonBuilder = GsonBuilder()
            gsonBuilder.registerTypeAdapter(Rates::class.java, RatesJsonDeserializer())
            val gson = gsonBuilder.create()
            return GsonConverterFactory.create(gson)
        }

        fun create(context: Context?): Service {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(ConnectivityInterceptor(context))
                .addInterceptor(QueryInterceptor())
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(createGsonConverter())
                .baseUrl(DOMAIN)
                .build()

            return retrofit.create(Service::class.java)
        }
    }
}