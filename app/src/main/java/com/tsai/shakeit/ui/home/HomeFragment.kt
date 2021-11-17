package com.tsai.shakeit.ui.home

import android.Manifest.permission.*
import android.animation.IntEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.libraries.maps.*
import com.google.android.libraries.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.google.maps.android.ui.IconGenerator
import com.tsai.shakeit.BuildConfig.DIRECTION_API_KEY
import com.tsai.shakeit.MainViewModel
import com.tsai.shakeit.R
import com.tsai.shakeit.ShakeItApplication
import com.tsai.shakeit.data.Favorite
import com.tsai.shakeit.data.Product
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.databinding.FragmentHomeBinding
import com.tsai.shakeit.ext.getVmFactory
import com.tsai.shakeit.ext.mToast
import com.tsai.shakeit.ext.visibility
import com.tsai.shakeit.network.LoadApiStatus
import com.tsai.shakeit.permission.AppPermissions
import com.tsai.shakeit.ui.home.comment.CommentPagerAdapter
import com.tsai.shakeit.ui.home.search.SearchAdapter
import com.tsai.shakeit.ui.menu.MenuFragmentDirections
import com.tsai.shakeit.util.*
import kotlin.properties.Delegates


class HomeFragment : Fragment(), OnMapReadyCallback {

    private val viewModel by viewModels<HomeViewModel> {
        getVmFactory()
    }

    // Declare a variable for the cluster manager.
    private lateinit var telUri: Uri
    private lateinit var binding: FragmentHomeBinding
    private lateinit var mMap: GoogleMap
    private lateinit var appPermission: AppPermissions
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var bottomSheetNavBehavior: BottomSheetBehavior<View>
    private lateinit var mainViewModel: MainViewModel
    private lateinit var selectedShop: Shop
    private lateinit var polyLine: Polyline
    private lateinit var mContext: Context
    private lateinit var result: List<Product>
    private lateinit var allShopName: List<String>
    private lateinit var allShopData: List<Shop>
    private lateinit var allProductList: List<Product>

    private var queryShopName: String = ""
    private var lat by Delegates.notNull<Double>()
    private var lon by Delegates.notNull<Double>()
    private val vAnimator = ValueAnimator()
    var locationPermissionGranted = false

    private val fromTop: Animation =
        AnimationUtils.loadAnimation(ShakeItApplication.instance, R.anim.slidedown)
    private val toTopGone: Animation =
        AnimationUtils.loadAnimation(ShakeItApplication.instance, R.anim.slideup)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        if (mainViewModel.dbFilterShopList.value.isNullOrEmpty()) {
            mainViewModel.getFilterList()
        }

        if (mainViewModel.currentFragmentType.value == CurrentFragmentType.FAVORITE) {
            mainViewModel.selectedShop.observe(viewLifecycleOwner, {
                selectedShop = it
            })
        }

        if (mainViewModel.currentFragmentType.value == CurrentFragmentType.ORDER_DETAIL) {
            mainViewModel.selectedShop.observe(viewLifecycleOwner, {
                selectedShop = it
            })
        }
    }

    override fun onStart() {
        super.onStart()
        binding.mainViewModel = mainViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        appPermission = AppPermissions()
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.binding = binding
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetNavBehavior = BottomSheetBehavior.from(binding.bottomSheetNav)
        bottomSheetNavBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        mContext = binding.root.context

        //get map
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //check has favorite
        viewModel.favorite.observe(viewLifecycleOwner, {
            viewModel.mShopId?.let { viewModel.checkHasFavorite() }
        })

        //when selected shop
        viewModel.selectedShop.observe(viewLifecycleOwner, { shop ->
            val favorite = Favorite(shop = shop, user_Id = UserInfo.userId)
            selectedShop = shop
            binding.favorite = favorite
            binding.shop = shop
            viewModel.checkHasFavorite()

            binding.telBtn.setOnClickListener {
                telUri = Uri.parse("tel:${shop.tel}");
                startActivity(Intent(Intent.ACTION_DIAL, telUri))
            }

            binding.apply {
                viewpagerHome.let {
                    tabsHome.setupWithViewPager(it)
                    it.adapter = CommentPagerAdapter(childFragmentManager, shopId = shop.shop_Id)
                    it.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabsHome))
                }
            }
        })

        viewModel.currentFragmentType.observe(viewLifecycleOwner, {
            when (it) {
                CurrentFragmentType.HOME ->
                    if (viewModel.status.value != LoadApiStatus.LOADING &&
                        binding.constraintLayout2.visibility != View.VISIBLE
                    ) {
                        toolbarVisible()
                    }
                CurrentFragmentType.HOME_NAV -> toolbarGone()
                else -> {
                }
            }
        })

        //observe onClick stop navigation
        viewModel.mapNavState.observe(viewLifecycleOwner, {
            it?.let {
                stopMapNavigation()
                val currentPosition = LatLng(lat, lon)
                mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        currentPosition,
                        18F
                    )
                )
            }
        })

        //nav Menu
        viewModel.navToMenu.observe(viewLifecycleOwner, {
            it?.let {
                findNavController().navigate(
                    MenuFragmentDirections.navToMenu(it, "")
                )
            }
        })

        //nav AddShop
        viewModel.navToAddShop.observe(viewLifecycleOwner, {
            it?.let { findNavController().navigate(HomeFragmentDirections.navToAddShop()) }
        })

        //nav Setting
        viewModel.navToSetting.observe(viewLifecycleOwner, {
            it?.let {
                findNavController().navigate(
                    HomeFragmentDirections.navToSetting(
                        viewModel.shopLiveData.value!!.toTypedArray()
                    )
                )
            }
        })

        //addMarker to map
        viewModel.shopLiveData.observe(viewLifecycleOwner, { shopData ->

            allShopName = shopData.map { it.name }.distinct()
            allShopData = shopData

            // observe when get currentPosition
            if (mainViewModel.currentFragmentType.value == CurrentFragmentType.ORDER_DETAIL) {

                viewModel.mode.value?.let { mode -> getDirection(mode) }

                viewModel.getDirectionDone.observe(viewLifecycleOwner, {
                    if (mainViewModel.currentFragmentType.value == CurrentFragmentType.ORDER_DETAIL) {
                        it?.let {
                            viewModel.drawPolyLine()
                            viewModel.getDirectionDone.value = null
                        }
                    }
                })
            }

            mainViewModel.dbFilterShopList.observe(viewLifecycleOwner, { dbList ->

                //if nav From Order
                if (mainViewModel.currentFragmentType.value == CurrentFragmentType.ORDER_DETAIL) {
                    viewModel.getCurrentPosition(LatLng(lat, lon))
                }

                setSearchBar(dbList)

                if (queryShopName.isEmpty()) {
                    addMapMarker(dbList)
                } else {
                    addMapMarker(allShopName.filter { it != queryShopName })
                }

            })
        })


        //observe move camera
        viewModel.moveCamera.observe(viewLifecycleOwner, {
            it?.let { moveCameraToCurrentLocation() }
        })

        //obseve userSetting traffic time
        viewModel.userSettingTime.observe(viewLifecycleOwner, {
            UserInfo.userCurrentSettingTrafficTime = it
        })

        //navBtn onclick
        binding.navBtn.setOnClickListener {
            viewModel.drawPolyLine()
        }

        // observe get google direction done
        viewModel.options.observe(viewLifecycleOwner, {

            if (this::polyLine.isInitialized) {
                polyLine.remove()
            }

            if (mainViewModel.currentFragmentType.value != CurrentFragmentType.MENU) {
                it?.let {
                    bottomSheetNavBehavior.isDraggable = false
                    bottomSheetBehavior.halfExpandedRatio = 0.0001f
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                    mainViewModel.currentFragmentType.value = CurrentFragmentType.HOME_NAV
                    viewModel.currentFragmentType.value = CurrentFragmentType.HOME_NAV
                    bottomSheetNavBehavior.state = BottomSheetBehavior.STATE_EXPANDED

                    val currentPosition = LatLng(lat, lon)

                    mMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            currentPosition,
                            17F,
                        )
                    )
                    polyLine = mMap.addPolyline(it)
                }
            }
        })

        //when change traffic mode getDirection
        viewModel.mode.observe(viewLifecycleOwner, {
            if (locationPermissionGranted) {
                Logger.d("mode observe $it")
                val currentPosition = LatLng(lat, lon)
                mapSearchAnimation(currentPosition)
                viewModel.getShopData(currentPosition, "search")
            }
        })

        //observe User Search Product
        viewModel.userSearchProduct.observe(viewLifecycleOwner, { product ->
            queryShopName = product.shop_Name
            result = allProductList.filter { it.shop_Name.contains(queryShopName) }
                .toMutableList().sortedBy { it.type }
            doSearch()
        })

        //traffic time editText action_done
        binding.editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == IME_ACTION_DONE) {
                val currentPosition = LatLng(lat, lon)
                mapSearchAnimation(currentPosition)
                viewModel.getShopData(currentPosition, "search")
            }
            false
        }

        setBackPressedBehavior()
        binding.addShopFab.isExtended = false
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext)
        return binding.root
    }

    private fun addMapMarker(
        dbList: List<String>
    ) {
        mMap.clear()
        vAnimator.pause()
        allShopData.forEach { shop ->
            if (!dbList.contains(shop.name)) {
                val newPosition = LatLng(shop.lat, shop.lon)
                mMap.addMarker(
                    MarkerOptions().position(newPosition).snippet(shop.shop_Id)
                        .icon(
                            BitmapDescriptorFactory.fromBitmap(
                                getMarkerIconWithLabel(
                                    shop.name.substring(
                                        0,
                                        1
                                    ), shop.branch
                                )
                            )
                        )
                )
            }
        }
    }

    //set backPressed behavior
    private fun setBackPressedBehavior() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
                    stopMapNavigation()
                } else {
                    requireActivity().finish()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            callback
        )
    }

    //stop Map navigation
    private fun stopMapNavigation() {
        bottomSheetNavBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        if (this::polyLine.isInitialized) {
            polyLine.remove()
        }
    }

    //map ready
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        appPermission.askPermission(this)
        moveCameraToSelectedShop()
        setBottomSheetBehavior()
    }

    //when nav from favorite move camera
    private fun moveCameraToSelectedShop() {
        if (this::selectedShop.isInitialized &&
            mainViewModel.currentFragmentType.value != CurrentFragmentType.MENU
        ) {
            val position = LatLng(selectedShop.lat, selectedShop.lon)
            mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    position,
                    20F
                )
            )
        }
    }

    //set searchBar
    private fun setSearchBar(dbList: List<String>) {

        val listAdapter = SearchAdapter(viewModel)

        viewModel.allproduct.observe(viewLifecycleOwner, { list ->
            allProductList = list
            listAdapter.submitList(list.sortedBy { it.type })
        })

        binding.searchView.setOnQueryTextFocusChangeListener { view, b ->
            if (b) {
                binding.searchList.visibility(1)
            } else {
                binding.searchList.visibility(0)
            }
        }

        val clearButton: ImageView =
            binding.searchView.findViewById(androidx.appcompat.R.id.search_close_btn)

        clearButton.setOnClickListener {
            if (binding.searchView.query.isEmpty()) {
                binding.searchView.isIconified = true
            } else {

                addMapMarker(dbList)
                queryShopName = ""
                binding.searchView.setQuery("", false)
            }
        }

        binding.searchList.visibility(0)

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                doSearch()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { text ->

                    result = allProductList.filter { it.type.contains(text) }
                        .toMutableList().sortedBy { it.type }

                    listAdapter.submitList(result)

                    if (result.isEmpty()) {
                        result = allProductList.filter { it.shop_Name.contains(text) }
                            .toMutableList().sortedBy { it.type }
                        listAdapter.submitList(result)
                    }

                    if (result.isEmpty()) {
                        result = allProductList.filter { it.name.contains(text) }
                            .toMutableList().sortedBy { it.type }
                        listAdapter.submitList(result)
                    }

                }
                return false
            }
        })
        binding.searchList.adapter = listAdapter
    }

    private fun doSearch() {
        queryShopName = result.first().shop_Name
        binding.searchView.setQuery(result.first().name, false)
        mToast("正在搜尋附近含有 - ${result.first().name} 的店家 ")

        if (!allShopName.isNullOrEmpty()) {
            if (result.isNotEmpty() && queryShopName.isNotEmpty()) {
                val searchName = allShopName.filter { it != queryShopName }
                addMapMarker(searchName)
            }
        }

        binding.searchView.clearFocus()
        binding.searchList.visibility(0)
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
            if (mainViewModel.currentFragmentType.value != CurrentFragmentType.HOME_NAV) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                mainViewModel.currentFragmentType.value = CurrentFragmentType.HOME_DIALOG
                viewModel.currentFragmentType.value = CurrentFragmentType.HOME_DIALOG
                viewModel.getSelectedShopSnippet(it.snippet)
                getDirection("${viewModel.mode.value}")
            }
            return@setOnMarkerClickListener true
        }

        //map onClick
        mMap.setOnMapClickListener {
            if (mainViewModel.currentFragmentType.value != CurrentFragmentType.HOME_NAV) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
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
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        if (mainViewModel.currentFragmentType.value != CurrentFragmentType.ORDER_DETAIL) {
                            mainViewModel.currentFragmentType.value = CurrentFragmentType.HOME
                            viewModel.currentFragmentType.value = CurrentFragmentType.HOME
                        }
                    }
                    else -> {
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

                if (slideOffset > 0.4f) {
                    if (x == 0) {
                        x = 1
                        toolbarGone()
                    }
                }

                if (x == 1 && slideOffset < 0.4f) {
                    x = 0
                    if (viewModel.currentFragmentType.value != CurrentFragmentType.HOME_NAV) {
                        toolbarVisible()
                    }
                }
            }
        })
    }

    //set toolbar visibility
    private fun toolbarVisible() {
        binding.constraintLayout2.startAnimation(fromTop)
        binding.constraintLayout2.visibility(1)
    }

    //set toolbar visibility
    private fun toolbarGone() {
        binding.constraintLayout2.startAnimation(toTopGone)
        binding.constraintLayout2.visibility(0)
    }

    // get location
    @SuppressLint("MissingPermission")
    fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                setMapUI()
                val locationRequest = LocationRequest.create()
                locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                mFusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult?) {
                            locationResult ?: return
                            lat = locationResult.lastLocation.latitude
                            lon = locationResult.lastLocation.longitude
                            val currentPosition = LatLng(lat, lon)
                            viewModel.getShopData(currentPosition)
                            if (!::selectedShop.isInitialized) {
                                mMap.moveCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        currentPosition,
                                        15F
                                    )
                                )
                            }
                        }
                    },
                    Looper.getMainLooper()
                )
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    //set google map UI
    @SuppressLint("MissingPermission")
    private fun setMapUI() {
        mMap.uiSettings.isMyLocationButtonEnabled = false
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.isMyLocationEnabled = true
    }

    //getDirection
    private fun getDirection(mode: String) {

        if (locationPermissionGranted) {
            val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=" + lat + "," + lon +
                    "&destination=" + selectedShop.lat + "," + selectedShop.lon +
                    "&mode=" + mode +
                    "&key=" + DIRECTION_API_KEY +
                    "&language=zh-TW"

            viewModel.getDirection(url, mode)

        }
    }

    //move camera
    private fun moveCameraToCurrentLocation() {
        val currentPosition = LatLng(lat, lon)

        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                currentPosition,
                18F
            )
        )

    }

    //map search animation
    private fun mapSearchAnimation(currentPosition: LatLng) {

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
            circle.radius = (animatedFraction * viewModel.distance).toDouble()
        }
        vAnimator.start()
    }

    private fun getMarkerIconWithLabel(label: String, branch: String): Bitmap {
        val iconGenerator = IconGenerator(mContext)
        val markerView: View =
            LayoutInflater.from(mContext).inflate(R.layout.map_marker, null)
        val imgMarker = markerView.findViewById<ImageView>(R.id.mapIcon)
        val tvLabel = markerView.findViewById<TextView>(R.id.marker_shop)
        val tvLabel2 = markerView.findViewById<TextView>(R.id.textView38)
        imgMarker.setImageResource(R.drawable.location64)
        tvLabel.text = label
        tvLabel2.text = branch
        iconGenerator.setContentView(markerView)
        iconGenerator.setBackground(null)
        return iconGenerator.makeIcon(label)
    }
}





