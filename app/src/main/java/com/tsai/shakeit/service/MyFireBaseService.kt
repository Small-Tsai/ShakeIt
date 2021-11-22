package com.tsai.shakeit.service

import android.content.SharedPreferences
import android.util.Log
import com.application.isradeleon.notify.Notify
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tsai.shakeit.R
import com.tsai.shakeit.ShakeItApplication
import com.tsai.shakeit.data.OrderProduct
import com.tsai.shakeit.data.Product
import com.tsai.shakeit.data.User
import com.tsai.shakeit.data.source.remote.ShakeItRemoteDataSource
import com.tsai.shakeit.util.Logger
import com.tsai.shakeit.util.ORDERS
import com.tsai.shakeit.util.ORDER_PRODUCT
import com.tsai.shakeit.util.UserInfo

class MyFirebaseService : FirebaseMessagingService() {

    companion object {
        var sharedPref: SharedPreferences? = null

        var token: String?
            get() {
                return sharedPref?.getString("token", "")
            }
            set(value) {
                sharedPref?.edit()?.putString("token", value)?.apply()
            }
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        token = newToken
        Logger.d("newToken = $newToken")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        message.data["title"]?.let {
            message.data["message"]?.let { it1 ->
                Notify
                    .build(this)
                    .setTitle(it)
                    .setContent(it1)
                    .setSmallIcon(R.drawable.order)
                    .setLargeIcon(R.drawable.app_icon)
                    .setColor(R.color.loginBlue)
                    .setAutoCancel(true)
                    .setImportance(Notify.NotifyImportance.HIGH)
                    .show()
            }
        }
    }
}