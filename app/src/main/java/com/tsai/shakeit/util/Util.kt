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
}
