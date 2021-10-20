package com.tsai.shakeit.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.tsai.shakeit.R
import com.tsai.shakeit.databinding.FragmentHomeBinding
import com.tsai.shakeit.util.MyContext
import kotlin.properties.Delegates


const val TAG = "tsai"
const val REQUEST_LOCATION_PERMISSION = 0
const val REQUEST_ENABLE_GPS = 1


class HomeFragment : Fragment(), OnMapReadyCallback {

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private var lat by Delegates.notNull<Double>()
    private var lon by Delegates.notNull<Double>()
    private var locationPermissionGranted = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        viewModel.binding = binding

        val mapFragment =
            childFragmentManager.findFragmentById(binding.map.id) as SupportMapFragment
        mapFragment.getMapAsync(this)

        viewModel.isWalkOrRide.observe(viewLifecycleOwner, Observer {
            when (it) {
                true -> onAddButtonClicked(it)
                false -> {
                    onAddButtonClicked(it)
                    viewModel.isNull()
                }
            }
        })

        val mContext = binding.root.context
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext)
        return binding.root
    }

    fun onAddButtonClicked(b: Boolean) {
        setVisibility(b)
        setAnimation(b)
    }

    val fromTop = AnimationUtils.loadAnimation(MyContext.appContext, R.anim.from_top_anim)
    val toBottom = AnimationUtils.loadAnimation(MyContext.appContext, R.anim.to_bottom_anim)
    private fun setAnimation(b: Boolean) {
        if (b) {
            binding.rideFab.startAnimation(toBottom)
        } else {
            binding.rideFab.startAnimation(fromTop)
        }
    }

    private fun setVisibility(b: Boolean) {
        if (b) binding.rideFab.visibility = View.VISIBLE
        else binding.rideFab.visibility = View.GONE
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val myPositionBtn = binding.map.findViewById<View>("2".toInt())
        myPositionBtn.setPadding(0,1500,0,0)
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener {
            findNavController().navigate(HomeDialogFragmentDirections.navToHomeDialog())
            return@setOnMarkerClickListener true
        }
        val newPosition = LatLng(25.03896015328872,121.57038585761444)
        mMap.addMarker(MarkerOptions().position(newPosition).title("213213"))
        checkGPSState()
    }

    private fun getLocationPermission() {
        //檢查權限
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //已獲取到權限
            Toast.makeText(context, "已獲取到位置權限，可以準備開始獲取經緯度", Toast.LENGTH_SHORT).show()
            locationPermissionGranted = true
            checkGPSState()
        } else {
            //詢問要求獲取權限
            requestLocationPermission()
        }
    }

    override fun onRequestPermissionsResult(
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
    }


//    private fun askPermission() {
//        PermissionX.init(this)
//            .permissions(
//                android.Manifest.permission.ACCESS_FINE_LOCATION,
//                android.Manifest.permission.CALL_PHONE,
//                android.Manifest.permission.INTERNET
//            )
//            .onExplainRequestReason { scope, deniedList ->
//                scope.showRequestReasonDialog(
//                    deniedList,
//                    "主要功能需要使用到以下權限",
//                    "確定",
//                    "取消"
//                )
//            }
//            .onForwardToSettings { scope, deniedList ->
//                scope.showForwardToSettingsDialog(
//                    deniedList,
//                    "您需要到設定頁面手動開啟權限",
//                    "確定",
//                    "取消"
//                )
//            }
//            .request { allGranted, grantedList, deniedList ->
//                if (allGranted) {
//                    Toast.makeText(context, "所有權限已開啟", Toast.LENGTH_LONG).show()
//
//                } else {
//                    Toast.makeText(
//                        context,
//                        "These permissions are denied: $deniedList",
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//            }
//    }

    private fun requestLocationPermission() {
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


    private fun checkGPSState() {
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
            getDeviceLocation()
            Toast.makeText(context, "已獲取到位置權限且GPS已開啟，可以準備開始獲取經緯度", Toast.LENGTH_SHORT).show()
        }
    }


    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted
            ) {
                val locationRequest = LocationRequest()
                locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                //更新次數，若沒設定，會持續更新
                //locationRequest.numUpdates = 1
                mFusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult?) {
                            locationResult ?: return
                            Log.d(
                                "HKT",
                                "緯度:${locationResult.lastLocation.latitude} , 經度:${locationResult.lastLocation.longitude} "
                            )
                            lat = locationResult.lastLocation.latitude
                            lon = locationResult.lastLocation.longitude
                            mMap.isMyLocationEnabled = true
                            mMap.uiSettings.isMyLocationButtonEnabled = true
                            val currentPosition = LatLng(lat, lon)
                            mMap.addMarker(MarkerOptions().position(currentPosition).title("Here"))
                            mMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    currentPosition,
                                    18F
                                )
                            );
                        }
                    },
                    null
                )

            } else {
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
}