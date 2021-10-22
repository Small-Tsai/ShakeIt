package com.tsai.shakeit.util

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.tsai.shakeit.ShakeItApplication


object Util {

    @SuppressLint("MissingPermission")
    fun isInternetConnected(): Boolean {
        val cm = ShakeItApplication.instance
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    fun getString(resourceId: Int): String {
        return ShakeItApplication.instance.getString(resourceId)
    }

    fun getColor(resourceId: Int): Int {
        return ShakeItApplication.instance.getColor(resourceId)
    }
}
