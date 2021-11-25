package com.tsai.shakeit.util

import com.google.android.libraries.maps.model.LatLng
import com.tsai.shakeit.app.DRIVING

object UserInfo {

    var userId = ""

    var userName = ""

    var userImage = ""

    var userCurrentSelectTrafficMode = DRIVING

    var userCurrentSettingTrafficTime = "60"

    var userCurrentLat: Double = 0.0

    var userCurrentLng: Double = 0.0

    val userCurrentLocation: LatLng
        get() {
            return LatLng(userCurrentLat, userCurrentLng)
        }
}
