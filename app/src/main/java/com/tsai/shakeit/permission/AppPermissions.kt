package com.tsai.shakeit.permission

import android.Manifest
import android.content.Context
import android.location.LocationManager
import com.permissionx.guolindev.PermissionX
import com.tsai.shakeit.R
import com.tsai.shakeit.ShakeItApplication
import com.tsai.shakeit.ext.mToast
import com.tsai.shakeit.ui.home.HomeFragment
import com.tsai.shakeit.util.Logger
import com.tsai.shakeit.util.Util
import java.lang.Exception

class AppPermissions {

    var lm =
        ShakeItApplication.instance.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var gpsEnabled = false
    private var networkEnabled = false

    // require permission
    fun askPermission(fragment: HomeFragment) {

        PermissionX.init(fragment)
            .permissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(
                    deniedList,
                    Util.getString(R.string.explainPermission),
                    Util.getString(R.string.yes),
                    Util.getString(R.string.no)
                )
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    Util.getString(R.string.explainSettingPermission),
                    Util.getString(R.string.yes),
                    Util.getString(R.string.no)
                )
            }
            .request { allGranted, _, deniedList ->
                if (allGranted) {
                    fragment.locationPermissionGranted = true
                    fragment.getDeviceLocation()

                    try {
                        gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    } catch (e: Exception) {
                        Logger.e("gps $e")
                    }

                    try {
                        networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                    } catch (e: Exception) {
                        Logger.e("network $e")
                    }

                    if (!gpsEnabled && !networkEnabled) {
                        mToast(Util.getString(R.string.need_open_currentLocation))
                    }

                } else {
                    mToast("已拒絕以下權限: $deniedList")
                }
            }
    }
}