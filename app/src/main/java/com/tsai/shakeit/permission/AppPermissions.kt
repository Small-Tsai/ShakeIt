package com.tsai.shakeit.permission

import android.Manifest
import androidx.fragment.app.Fragment
import com.permissionx.guolindev.PermissionX
import com.tsai.shakeit.R
import com.tsai.shakeit.ext.mToast
import com.tsai.shakeit.ui.home.HomeFragment
import com.tsai.shakeit.util.Util

class AppPermissions {



    // require permission
    fun askPermission(fragment: HomeFragment) {

        PermissionX.init(fragment)
            .permissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_EXTERNAL_STORAGE

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
                } else {
                    mToast("已拒絕以下權限: $deniedList")
                }
            }
    }
}