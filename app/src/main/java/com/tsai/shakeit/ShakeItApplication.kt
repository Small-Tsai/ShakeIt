package com.tsai.shakeit

import android.app.Application
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.util.ServiceLocator
import kotlin.properties.Delegates

class ShakeItApplication : Application() {

    // Depends on the flavor,
    val shakeItRepository: ShakeItRepository
        get() = ServiceLocator.provideTasksRepository(this)

    companion object {
        var instance: ShakeItApplication by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}