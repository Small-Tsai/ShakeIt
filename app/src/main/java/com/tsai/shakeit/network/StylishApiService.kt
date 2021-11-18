package com.tsai.shakeit.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tsai.shakeit.BuildConfig
import com.tsai.shakeit.BuildConfig.FIREBASE_SERVER_KEY
import com.tsai.shakeit.data.directionPlaceModel.Direction
import com.tsai.shakeit.data.notification.PushNotification
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private const val BASE_URL = "https://maps.googleapis.com/"
private const val FIREBASE_URL = "https://fcm.googleapis.com"
private const val CONTENT_TYPE = "application/json"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val client = OkHttpClient.Builder()
    .addInterceptor(
        HttpLoggingInterceptor().apply {
            level = when (BuildConfig.LOGGER_VISIABLE) {
                true -> HttpLoggingInterceptor.Level.BODY
                false -> HttpLoggingInterceptor.Level.NONE
            }
        }
    ).build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .client(client)
    .build()

private val retrofitNotify = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(FIREBASE_URL)
    .client(client)
    .build()


interface ShakeItApiService {

    @GET
    suspend fun getDirection(@Url url: String): Direction

    @Headers("Authorization: key=$FIREBASE_SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(@Body notification: PushNotification): Response<ResponseBody>

}

object ShakeItApi {
    val retrofitService: ShakeItApiService by lazy { retrofit.create(ShakeItApiService::class.java) }
    val firebaseService: ShakeItApiService by lazy { retrofitNotify.create(ShakeItApiService::class.java) }
}
