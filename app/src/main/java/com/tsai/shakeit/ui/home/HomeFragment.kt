package com.tsai.shakeit.ui.home

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.libraries.maps.*
import com.google.android.libraries.maps.model.*
import com.google.maps.android.ui.IconGenerator
import com.permissionx.guolindev.PermissionX
import com.tsai.shakeit.R
import com.tsai.shakeit.ShakeItApplication
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.databinding.FragmentHomeBinding
import com.tsai.shakeit.ext.getVmFactory
import kotlin.properties.Delegates


const val TAG = "tsai"
const val REQUEST_LOCATION_PERMISSION = 0
const val REQUEST_ENABLE_GPS = 1


class HomeFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private val viewModel by viewModels<HomeViewModel> {
        getVmFactory()
    }

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

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        viewModel.binding = binding

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
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

    private fun onAddButtonClicked(b: Boolean) {
        setVisibility(b)
        setAnimation(b)
    }

    val fromTop = AnimationUtils.loadAnimation(ShakeItApplication.instance, R.anim.from_top_anim)
    val toBottom = AnimationUtils.loadAnimation(ShakeItApplication.instance, R.anim.to_bottom_anim)
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
        askPermission()

        mMap = googleMap
//        mMap.setMapStyle(MapStyleOptions(getResources()
//            .getString(R.string.style_json)));


        viewModel.shopData.observe(viewLifecycleOwner, Observer { shopData ->
            shopData.forEach { shop ->
                val newPosition = LatLng(shop.lat, shop.lon)
                val iconGen = IconGenerator(binding.root.context)
                mMap.addMarker(
                    MarkerOptions().position(newPosition).snippet(shop.branch)
//                        .icon(BitmapDescriptorFactory.fromBitmap(iconGen.makeIcon(shop.name)))
                )
            }
        })

        viewModel.snippet.observe(viewLifecycleOwner, Observer { snippet ->
            snippet?.let {
                val filterShop = viewModel.shopData.value?.filter { it.branch == snippet }?.first()
                filterShop?.let {
                    viewModel.navDone()
                    findNavController().navigate(HomeDialogFragmentDirections.navToHomeDialog(filterShop))
                }
            }
        })


        mMap.setOnMarkerClickListener {

            viewModel.navToDetail(it.snippet)
            return@setOnMarkerClickListener true
        }

        mMap.uiSettings.isZoomControlsEnabled = true
        val map = view?.findViewById<View>(R.id.map)
        val myPositionBtn = map?.findViewById<View>("2".toInt())
        val rlp = myPositionBtn?.layoutParams as RelativeLayout.LayoutParams
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        rlp.setMargins(0, 0, 30, 280)

    }

    private fun askPermission() {
        PermissionX.init(this)
            .permissions(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.CALL_PHONE,
                android.Manifest.permission.INTERNET
            )
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(
                    deniedList,
                    "主要功能需要使用到以下權限",
                    "確定",
                    "取消"
                )
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    "您需要到設定頁面手動開啟權限",
                    "確定",
                    "取消"
                )
            }
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    locationPermissionGranted = true
                    getDeviceLocation()
                    Toast.makeText(context, "所有權限已開啟", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        context,
                        "已拒絕以下權限: $deniedList",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted
            ) {
                val locationRequest = LocationRequest()
                locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

                mFusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult?) {
                            locationResult ?: return
                            lat = locationResult.lastLocation.latitude
                            lon = locationResult.lastLocation.longitude

                            mMap.uiSettings.isMyLocationButtonEnabled = true
                            mMap.isMyLocationEnabled = true

                            val currentPosition = LatLng(lat, lon)
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
                askPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        TODO()
    }


}




