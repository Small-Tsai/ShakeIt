package com.tsai.shakeit.util

import android.animation.IntEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.view.animation.AccelerateDecelerateInterpolator
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.model.Circle
import com.google.android.libraries.maps.model.CircleOptions
import com.google.android.libraries.maps.model.LatLng
import com.tsai.shakeit.R
import com.tsai.shakeit.ShakeItApplication


object Util {

    @SuppressLint("MissingPermission")
    fun isInternetConnected(): Boolean {
        val cm = ShakeItApplication.instance
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    fun getString(resourceId: Int): String {
        return ShakeItApplication.instance.getString(resourceId)
    }

    fun getColor(resourceId: Int): Int {
        return ShakeItApplication.instance.getColor(resourceId)
    }

    //map search animation
    fun startSearchAnimationOnMap(
        currentPosition: LatLng,
        mMap: GoogleMap,
        vAnimator: ValueAnimator,
        distance: Double
    ) {

        val circle: Circle = mMap.addCircle(
            CircleOptions().center(currentPosition)
                .strokeColor(Util.getColor(R.color.blue)).radius(2000.0)
        )

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 14f))
        vAnimator.repeatCount = ValueAnimator.INFINITE
        vAnimator.repeatMode = ValueAnimator.RESTART /* PULSE */
        vAnimator.setIntValues(0, 1000)
        vAnimator.duration = 1200
        vAnimator.setEvaluator(IntEvaluator())
        vAnimator.interpolator = AccelerateDecelerateInterpolator()
        vAnimator.addUpdateListener { valueAnimator ->
            val animatedFraction = valueAnimator.animatedFraction
            circle.radius = (animatedFraction * distance)
        }
        vAnimator.start()
    }

    fun decode(points: String): List<LatLng> {
        val len = points.length
        val path: MutableList<LatLng> = java.util.ArrayList(len / 2)
        var index = 0
        var lat = 0
        var lng = 0
        while (index < len) {
            var result = 1
            var shift = 0
            var b: Int
            do {
                b = points[index++].code - 63 - 1
                result += b shl shift
                shift += 5
            } while (b >= 0x1f)
            lat += if (result and 1 != 0) (result shr 1).inv() else result shr 1
            result = 1
            shift = 0
            do {
                b = points[index++].code - 63 - 1
                result += b shl shift
                shift += 5
            } while (b >= 0x1f)
            lng += if (result and 1 != 0) (result shr 1).inv() else result shr 1
            path.add(LatLng(lat * 1e-5, lng * 1e-5))
        }
        return path
    }
}
