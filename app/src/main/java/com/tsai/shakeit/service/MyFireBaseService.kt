package com.tsai.shakeit.service

import android.content.SharedPreferences
import com.application.isradeleon.notify.Notify
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tsai.shakeit.R

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