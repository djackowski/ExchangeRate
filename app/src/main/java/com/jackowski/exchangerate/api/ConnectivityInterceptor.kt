package com.jackowski.exchangerate.api

import android.content.Context
import com.jackowski.exchangerate.R
import com.jackowski.exchangerate.utils.InternetConnectivityManager
import okhttp3.Interceptor
import okhttp3.Response

class ConnectivityInterceptor(private val context: Context?): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if(!InternetConnectivityManager.isOnline(context)) {
            throw NoConnectivityException(context?.getString(R.string.no_internet_connection_message))
        }
        val builder = chain.request().newBuilder()
        return chain.proceed(builder.build())
    }
}