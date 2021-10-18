package com.tsai.shakeit.util

import android.app.Application
import android.content.Context

class MyContext : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    companion object {
        var appContext: Context? = null
            private set
    }
}