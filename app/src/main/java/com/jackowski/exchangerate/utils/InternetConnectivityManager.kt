package com.jackowski.exchangerate.utils

import android.content.Context
import android.net.ConnectivityManager

object InternetConnectivityManager {
    fun isOnline(context: Context?): Boolean {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = connectivityManager.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }
}