package com.tsai.shakeit.ext

import android.icu.text.SimpleDateFormat
import android.util.Log
import com.google.firebase.Timestamp
import java.util.*

fun Long.toDisplayFormat(): String {
    return SimpleDateFormat("yyyy.MM.dd hh:mm", Locale.TAIWAN).format(this)
}

fun Timestamp.toTimeFromTimeStamp(): String {
    return SimpleDateFormat("yyyy.MM.dd hh:mm", Locale.TAIWAN).format(this.toDate())
        .toString()
}