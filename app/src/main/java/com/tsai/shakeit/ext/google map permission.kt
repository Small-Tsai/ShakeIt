package com.tsai.shakeit.ext


//已使用 permissionX 取代

/* private fun getLocationPermission() {
       //檢查權限
       if (ActivityCompat.checkSelfPermission(
               requireContext(),
               Manifest.permission.ACCESS_FINE_LOCATION
           ) == PackageManager.PERMISSION_GRANTED
       ) {
           //已獲取到權限
//            Toast.makeText(context, "已獲取到位置權限，可以準備開始獲取經緯度", Toast.LENGTH_SHORT).show()
           locationPermissionGranted = true
           checkGPSState()
       } else {
           //詢問要求獲取權限
           requestLocationPermission()
       }
   }*/

/*override fun onRequestPermissionsResult(
    requestCode: Int, permissions: Array<out String>, grantResults: IntArray
) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    when (requestCode) {
        REQUEST_LOCATION_PERMISSION -> {
            if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //已獲取到權限
                    locationPermissionGranted = true
                    checkGPSState()
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            requireActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        //權限被永久拒絕
                        Toast.makeText(context, "位置權限已被關閉，功能將會無法正常使用", Toast.LENGTH_SHORT)
                            .show()

                        AlertDialog.Builder(context)
                            .setTitle("開啟位置權限")
                            .setMessage("此應用程式，位置權限已被關閉，需開啟才能正常使用")
                            .setPositiveButton("確定") { _, _ ->
                                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                startActivityForResult(intent, REQUEST_LOCATION_PERMISSION)
                            }
                            .setNegativeButton("取消") { _, _ -> requestLocationPermission() }
                            .show()
                    } else {
                        //權限被拒絕
                        Toast.makeText(context, "位置權限被拒絕，功能將會無法正常使用", Toast.LENGTH_SHORT).show()
                        requestLocationPermission()
                    }
                }
            }
        }
    }
}*/

/*private fun checkGPSState() {
    val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        AlertDialog.Builder(context)
            .setTitle("GPS 尚未開啟")
            .setMessage("使用此功能需要開啟 GSP 定位功能")
            .setPositiveButton("前往開啟",
                DialogInterface.OnClickListener { _, _ ->
                    startActivityForResult(
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_ENABLE_GPS
                    )
                })
            .setNegativeButton("取消", null)
            .show()
    } else {

//            Toast.makeText(context, "已獲取到位置權限且GPS已開啟，可以準備開始獲取經緯度", Toast.LENGTH_SHORT).show()
    }
}*/

/* private fun requestLocationPermission() {
     if (ActivityCompat.shouldShowRequestPermissionRationale(
             this.requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
         )
     ) {
         AlertDialog.Builder(context)
             .setMessage("此應用程式，需要位置權限才能正常使用")
             .setPositiveButton("確定") { _, _ ->
                 ActivityCompat.requestPermissions(
                     this.requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                     REQUEST_LOCATION_PERMISSION
                 )
             }
             .setNegativeButton("取消") { _, _ -> requestLocationPermission() }
             .show()
     } else {
         ActivityCompat.requestPermissions(
             this.requireActivity(),
             arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
             REQUEST_LOCATION_PERMISSION
         )
     }
 }
*/

