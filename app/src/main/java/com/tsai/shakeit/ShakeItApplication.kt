package com.tsai.shakeit

import android.app.Application
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.util.ServiceLocator
import kotlin.properties.Delegates

class ShakeItApplication : Application() {

    // Create Repo instance in Application
    val shakeItRepository: ShakeItRepository
        get() = ServiceLocator.provideTasksRepository(this)

    // Global context object
    companion object {
        var instance: ShakeItApplication by Delegates.notNull()
    }

    /**
     * Initialize [instance] when Application onCreate
     */
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    /**
     * For testing HomeViewModel
     */
    fun isLiveDataDesign() = true
}
