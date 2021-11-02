package com.tsai.shakeit.ui.home

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.libraries.maps.*
import com.google.android.libraries.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.permissionx.guolindev.PermissionX
import com.tsai.shakeit.MainViewModel
import com.tsai.shakeit.R
import com.tsai.shakeit.ShakeItApplication
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.databinding.FragmentHomeBinding
import com.tsai.shakeit.ext.getVmFactory
import com.tsai.shakeit.ext.mToast
import com.tsai.shakeit.ui.home.comment.CommentPagerAdapter
import com.tsai.shakeit.ui.menu.MenuFragmentDirections
import com.tsai.shakeit.util.CurrentFragmentType
import com.tsai.shakeit.util.Logger
import kotlin.properties.Delegates

class HomeFragment : Fragment(), OnMapReadyCallback {

    private val viewModel by viewModels<HomeViewModel> {
        getVmFactory()
    }

    private lateinit var binding: FragmentHomeBinding
    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private var lat by Delegates.notNull<Double>()
    private var lon by Delegates.notNull<Double>()
    private var locationPermissionGranted = false
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var mainViewModel: MainViewModel
    private lateinit var selectedShop: Shop

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        if (mainViewModel.currentFragmentType.value == CurrentFragmentType.FAVORITE_NAV_HOME) {
            mainViewModel.selectedFavorite.observe(viewLifecycleOwner, {
                selectedShop = it
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.binding = binding
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)

        val mContext = binding.root.context

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        viewModel.isWalkOrRide.observe(viewLifecycleOwner, {
            when (it) {
                true -> viewModel.onAddButtonClicked(it)
                false -> {
                    viewModel.onAddButtonClicked(it)
                    viewModel.isNull()
                }
            }
        })

        viewModel.hasNavToMenu.observe(viewLifecycleOwner, {
            it?.let {
                findNavController().navigate(
                    MenuFragmentDirections.navToMenu(it)
                )
            }
        })

        viewModel.Favorite.observe(viewLifecycleOwner, {
            viewModel.mShopId?.let { it1 -> viewModel.checkHasFavorite(it1) }
        })

        viewModel._selectedShop.observe(viewLifecycleOwner, { shop ->
            binding.shop = shop
            viewModel.checkHasFavorite(shop.shop_Id)

            binding.apply {

                viewpagerHome.let {
                    tabsHome.setupWithViewPager(it)
                    it.adapter = CommentPagerAdapter(childFragmentManager, shopId = shop.shop_Id)
                    it.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabsHome))
                }
            }
        })

        viewModel.navToAddShop.observe(viewLifecycleOwner, {
            it?.let { findNavController().navigate(HomeFragmentDirections.navToAddShop()) }
        })

        viewModel.navToSetting.observe(viewLifecycleOwner, {
            it?.let { findNavController().navigate(HomeFragmentDirections.navToSetting(viewModel.shopLiveData.value!!.toTypedArray())) }
        })

        viewModel.shopLiveData.observe(viewLifecycleOwner, { shopData ->

            mainViewModel.dbFilterShopList.observe(viewLifecycleOwner, { dbList ->
                mMap.clear()
                shopData.forEach { shop ->
                    if (!dbList.contains(shop.name)) {
                        val newPosition = LatLng(shop.lat, shop.lon)
//                val iconGen = IconGenerator(binding.root.context)
                        mMap.addMarker(
                            MarkerOptions().position(newPosition).snippet(shop.shop_Id)
//                        .icon(BitmapDescriptorFactory.fromBitmap(iconGen.makeIcon(shop.name)))
                        )
                    }
                }
            })

            binding.addShopFab.extend()
            Handler(Looper.getMainLooper()).postDelayed({
                binding.addShopFab.shrink()
            }, 1500)
        })

        binding.addShopFab.isExtended = false
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext)
        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (this::selectedShop.isInitialized) {
            setMapUI()

            val position = LatLng(selectedShop.lat, selectedShop.lon)
            mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    position,
                    20F
                )
            )
        }

        askPermission()
        setBottomSheetBehavior()
        setMyLocationButtonPosition()
    }

    private fun setMyLocationButtonPosition() {

        val map = view?.findViewById<View>(R.id.map)
        val myPositionBtn = map?.findViewById<View>("2".toInt())
        val rlp = myPositionBtn?.layoutParams as RelativeLayout.LayoutParams

        rlp.apply {
            addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
            addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            setMargins(0, 0, 30, 280)
        }
    }

    // custom BottomSheetUI
    private fun setBottomSheetBehavior() {

        // update total comment qty on bottomSheet
        mainViewModel.commentSize.observe(viewLifecycleOwner, {
            binding.commentSize.text = "($it)"
        })

        // calculate average rating
        mainViewModel.ratingAvg.observe(viewLifecycleOwner, {
            if (it.isNaN()) {
                binding.avgRating.text = "0"
                binding.ratingBar.rating = it
            } else {
                binding.avgRating.text = "%.1f".format(it)
                binding.ratingBar.rating = it
            }
        })

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        //marker onClick
        mMap.setOnMarkerClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            mainViewModel.currentFragmentType.value = CurrentFragmentType.HOME_DIALOG
            viewModel.getSelectedShopSnippet(it.snippet)
            return@setOnMarkerClickListener true
        }

        //map onClick
        mMap.setOnMapClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        //map camera move listner
        mMap.setOnCameraMoveListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.halfExpandedRatio = 0.14f
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            }
        }

        //bottomSheet CallBack
        var x = 0
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {

                    BottomSheetBehavior.STATE_EXPANDED -> {
                        Logger.d("expand")
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        mainViewModel.currentFragmentType.value = CurrentFragmentType.HOME
                    }
                    else -> {
                    }
                }
            }

            val fromTop =
                AnimationUtils.loadAnimation(ShakeItApplication.instance, R.anim.slidedown)
            val toTopGone =
                AnimationUtils.loadAnimation(ShakeItApplication.instance, R.anim.slideup)

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

                if (slideOffset > 0.4f) {
                    if (x == 0) {
                        Logger.d("$x")
                        x = 1
                        binding.apply {
                            idSearchView.startAnimation(toTopGone)
                            walkFab.startAnimation(toTopGone)
                            editText.startAnimation(toTopGone)
                            searchCv.startAnimation(toTopGone)
                            filterBar.startAnimation(toTopGone)
                            searchCv.visibility = View.GONE
                            filterBar.visibility = View.GONE
                            idSearchView.visibility = View.GONE
                            walkFab.visibility = View.GONE
                            editText.visibility = View.GONE
                        }

                    }
                }

                if (x == 1 && slideOffset < 0.4f) {
                    Logger.d("$x")
                    x = 0
                    binding.apply {
                        idSearchView.startAnimation(fromTop)
                        walkFab.startAnimation(fromTop)
                        editText.startAnimation(fromTop)
                        searchCv.startAnimation(fromTop)
                        filterBar.startAnimation(fromTop)
                        searchCv.visibility = View.VISIBLE
                        filterBar.visibility = View.VISIBLE
                        idSearchView.visibility = View.VISIBLE
                        walkFab.visibility = View.VISIBLE
                        editText.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    // require permission
    private fun askPermission() {

        PermissionX.init(this)
            .permissions(
                ACCESS_FINE_LOCATION,
                CALL_PHONE,
                INTERNET,
                READ_EXTERNAL_STORAGE

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
            .request { allGranted, _, deniedList ->
                if (allGranted) {
                    locationPermissionGranted = true
                    getDeviceLocation()
//                    mToast("所有權限已打開")
                } else {
                    mToast("已拒絕以下權限: $deniedList")
                }
            }
    }

    // get location
    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted && !this::selectedShop.isInitialized
            ) {
                val locationRequest = LocationRequest.create()
                locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

                mFusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult?) {
                            locationResult ?: return
                            lat = locationResult.lastLocation.latitude
                            lon = locationResult.lastLocation.longitude

                            setMapUI()

                            val currentPosition = LatLng(lat, lon)
                            mMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    currentPosition,
                                    18F
                                )
                            )
                        }
                    },
                    null
                )
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun setMapUI() {
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true
    }
}




