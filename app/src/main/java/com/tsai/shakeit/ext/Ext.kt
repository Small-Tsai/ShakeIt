package com.tsai.shakeit.ext

import android.icu.text.SimpleDateFormat
import android.widget.Toast
import com.google.firebase.Timestamp
import com.tsai.shakeit.ShakeItApplication
import java.util.*

fun Long.toDisplayFormat(): String {
    return SimpleDateFormat("yyyy.MM.dd hh:mm", Locale.TAIWAN).format(this)
}

fun Timestamp.toTimeFromTimeStamp(): String {
    return SimpleDateFormat("yyyy.MM.dd  hh:mm", Locale.TAIWAN).format(this.toDate())
        .toString()
}

fun mToast(text: String, duration: String?=null) {
    when(duration){
        null -> Toast.makeText(ShakeItApplication.instance, text, "0".toInt()).show()
        "long" -> Toast.makeText(ShakeItApplication.instance, text, "1".toInt()).show()
        "short" -> Toast.makeText(ShakeItApplication.instance, text, "0".toInt()).show()
    }
}




