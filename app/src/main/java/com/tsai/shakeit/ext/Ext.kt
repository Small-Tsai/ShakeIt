package com.tsai.shakeit.ext

import android.content.Intent
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.model.LatLng
import com.google.android.material.button.MaterialButton
import com.google.firebase.Timestamp
import com.tsai.shakeit.ShakeItApplication
import com.tsai.shakeit.util.GoogleCameraMoveMode
import java.util.*

fun Timestamp.toTimeFromTimeStamp(): String {
    return SimpleDateFormat("yyyy.MM.dd  hh:mm", Locale.TAIWAN).format(this.toDate())
        .toString()
}

fun myToast(text: String, duration: String? = null) {
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
    moveMode: GoogleCameraMoveMode,
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

// cropFloatPair = Pair<cropX,cropY>
fun MaterialButton.setOnClickChoosePhoto(
    fragment: Fragment,
    launcher: ActivityResultLauncher<Intent>,
    cropFloatPair: Pair<Float, Float> = Pair(0f, 0f),
) {
    setOnClickListener {
        ImagePicker.with(fragment)
            .galleryOnly()
            .crop(cropFloatPair.first, cropFloatPair.second)
            .compress(1024)
            .createIntent { intent ->
                launcher.launch(intent)
            }
    }
}

fun Uri.getBitmapFromUri() =
    ShakeItApplication.instance.contentResolver.openFileDescriptor(this, "r")?.use {
        BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
    }
