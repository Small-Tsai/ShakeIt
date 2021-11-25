package com.tsai.shakeit.app

import android.annotation.SuppressLint
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
import com.tsai.shakeit.ShakeItApplication.Companion.instance
import com.tsai.shakeit.ui.home.HomeFragment
import com.tsai.shakeit.util.UserInfo

class AppDevice(private val fragment: HomeFragment) {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // get location
    @SuppressLint("MissingPermission")
    fun getDeviceLocation(locationPermissionGranted: Boolean) {

        if (locationPermissionGranted) {

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(instance)

            try {
                val locationRequest = LocationRequest.create()
                locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult?) {
                            locationResult ?: return
                            UserInfo.userCurrentLat = locationResult.lastLocation.latitude
                            UserInfo.userCurrentLng = locationResult.lastLocation.longitude
                            fragment.setMapUI()
                        }
                    },
                    Looper.getMainLooper()
                )
            } catch (e: SecurityException) {
                Log.e("Exception: %s", e.message, e)
            }
        }
    }
}
