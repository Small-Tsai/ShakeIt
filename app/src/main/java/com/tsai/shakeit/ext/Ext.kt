package com.tsai.shakeit.ext

import android.Manifest
import android.icu.text.SimpleDateFormat
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.model.LatLng
import com.google.firebase.Timestamp
import com.permissionx.guolindev.PermissionX
import com.tsai.shakeit.R
import com.tsai.shakeit.ShakeItApplication
import com.tsai.shakeit.util.GoogleCameraMoveMode
import com.tsai.shakeit.util.Util
import com.tsai.shakeit.util.Util.getString
import java.util.*

fun Long.toDisplayFormat(): String {
    return SimpleDateFormat("yyyy.MM.dd hh:mm", Locale.TAIWAN).format(this)
}

fun Timestamp.toTimeFromTimeStamp(): String {
    return SimpleDateFormat("yyyy.MM.dd  hh:mm", Locale.TAIWAN).format(this.toDate())
        .toString()
}

fun mToast(text: String, duration: String? = null) {
    when (duration) {
        null -> Toast.makeText(ShakeItApplication.instance, text, "0".toInt()).show()
        "long" -> Toast.makeText(ShakeItApplication.instance, text, "1".toInt()).show()
    }
}

fun View.visibility(i: Int) {
    when (i) {
        0 -> visibility = View.GONE
        1 -> visibility = View.VISIBLE
        2 -> visibility = View.INVISIBLE
    }
}

fun GoogleMap.moveCamera(
    latLng: LatLng,
    zoomFloat: Float,
    moveMode: GoogleCameraMoveMode
) {
    when (moveMode) {

        GoogleCameraMoveMode.ANIMATE -> animateCamera(
            CameraUpdateFactory.newLatLngZoom(latLng, zoomFloat)
        )

        GoogleCameraMoveMode.IMMEDIATELY -> moveCamera(
            CameraUpdateFactory.newLatLngZoom(latLng, zoomFloat)
        )
    }

}







