package com.tsai.shakeit.util

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.tsai.shakeit.data.source.DefaultShakeItRepository
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.data.source.remote.ShakeItRemoteDataSource

object ServiceLocator {

    @Volatile
    var shakeItRepository: ShakeItRepository? = null
        @VisibleForTesting set

    fun provideTasksRepository(context: Context): ShakeItRepository {
        synchronized(this) {
            return shakeItRepository
                ?: createShakeItRepository(context)
        }
    }

    private fun createShakeItRepository(context: Context): ShakeItRepository {
        return DefaultShakeItRepository(
            ShakeItRemoteDataSource
        )
    }
}
