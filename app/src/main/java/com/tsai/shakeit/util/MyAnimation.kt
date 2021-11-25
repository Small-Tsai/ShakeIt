package com.tsai.shakeit.util

import android.animation.IntEvaluator
import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.model.Circle
import com.google.android.libraries.maps.model.CircleOptions
import com.google.android.libraries.maps.model.LatLng
import com.tsai.shakeit.R
import com.tsai.shakeit.ShakeItApplication

object MyAnimation {

    val fromTopVisibleAnimation: Animation =
        AnimationUtils.loadAnimation(ShakeItApplication.instance, R.anim.slidedown)

    val toTopGoneAnimation: Animation =
        AnimationUtils.loadAnimation(ShakeItApplication.instance, R.anim.slideup)

    // map search animation
    fun startSearchAnimation(
        currentPosition: LatLng,
        mMap: GoogleMap,
        vAnimator: ValueAnimator,
        distance: Double,
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
