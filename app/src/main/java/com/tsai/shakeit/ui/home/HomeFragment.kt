package com.tsai.shakeit.ui.home

import android.Manifest.permission.*
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.tsai.shakeit.app.AppPermissions
import com.tsai.shakeit.data.Favorite
import com.tsai.shakeit.data.Product
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.databinding.FragmentHomeBinding
import com.tsai.shakeit.ext.getVmFactory
import com.tsai.shakeit.ext.myToast
import com.tsai.shakeit.ext.moveCamera
import com.tsai.shakeit.ext.visibility
import com.tsai.shakeit.network.LoadApiStatus
import com.tsai.shakeit.ui.home.comment.CommentPagerAdapter
import com.tsai.shakeit.ui.home.search.SearchAdapter
import com.tsai.shakeit.ui.menu.MenuFragmentDirections
import com.tsai.shakeit.util.*

class HomeFragment : Fragment(), OnMapReadyCallback {

    val viewModel by viewModels<HomeViewModel> {
        getVmFactory()
    }

    private lateinit var telUri: Uri
    private lateinit var binding: FragmentHomeBinding
    private lateinit var mMap: GoogleMap
    private lateinit var appPermission: AppPermissions
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var bottomSheetNavBehavior: BottomSheetBehavior<View>
    private lateinit var mainViewModel: MainViewModel
    private lateinit var selectedShop: Shop
    private lateinit var polyLine: Polyline
    private lateinit var mContext: Context
    private lateinit var filteredProudctList: List<Product>
    private lateinit var allShopName: List<String>
    private lateinit var allShopData: List<Shop>
    private lateinit var allProductList: List<Product>
    private var queryShopName: String = ""
    private val vAnimator = ValueAnimator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Create Permission class for ask permission
        appPermission = AppPermissions()
    }

    override fun onStart() {
        super.onStart()
        // Binding mainViewModel to layout
        binding.mainViewModel = mainViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get mainViewModel
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        // Get user filtered shopList
        if (mainViewModel.firebaseFilteredShopList.value.isNullOrEmpty()) {
            mainViewModel.getFireBaseFilteredShopList()
        }

        // Initialize selected shop when user click shop from favorite page
        if (mainViewModel.currentFragmentType.value == CurrentFragmentType.FAVORITE) {
            mainViewModel.selectedShop.observe(viewLifecycleOwner, {
                selectedShop = it
            })
        }

        // Initialize selected shop when user click navigation from orderDetail page
        if (mainViewModel.currentFragmentType.value == CurrentFragmentType.ORDER_DETAIL) {
            mainViewModel.selectedShop.observe(viewLifecycleOwner, {
                selectedShop = it
            })
        }

        // Calculate average rating
        mainViewModel.ratingAvg.observe(viewLifecycleOwner, { ratingAvg ->
            if (ratingAvg.isNaN()) {
                binding.avgRating.text = "0"
                binding.ratingBar.rating = ratingAvg
            } else {
                "%.1f".format(ratingAvg).also { binding.avgRating.text = it }
                binding.ratingBar.rating = ratingAvg
            }
        })

        // Update total comment qty on bottomSheet
        mainViewModel.commentCount.observe(viewLifecycleOwner, { commentCount ->
            "($commentCount)".also { binding.commentCount.text = it }
        })

        // Custom backPressedBehavior
        setBackPressedBehavior()
        mContext = binding.root.context
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetNavBehavior = BottomSheetBehavior.from(binding.bottomSheetNav)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        //Create googleMap
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Observe when user select shop
        viewModel.selectedShop.observe(viewLifecycleOwner, { shop ->

            val favorite = Favorite(shop = shop, user_Id = UserInfo.userId)
            selectedShop = shop
            viewModel.checkHasFavorite()

            binding.apply {

                this.shop = shop
                this.favorite = favorite

                telBtn.setOnClickListener {
                    telUri = Uri.parse("tel:${shop.tel}")
                    startActivity(Intent(Intent.ACTION_DIAL, telUri))
                }

                viewpagerHome.let {
                    tabsHome.setupWithViewPager(it)
                    it.adapter = CommentPagerAdapter(childFragmentManager, shopId = shop.shop_Id)
                    it.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabsHome))
                }
            }
        })


        // Observe onClick stop navigation
        viewModel.mapNavState.observe(viewLifecycleOwner, {
            it?.let {
                stopMapNavigation()
                moveCameraToCurrentLocation(16f, GoogleCameraMoveMode.ANIMATE)
            }
        })

        // Nav to Menu
        viewModel.navToMenu.observe(viewLifecycleOwner, {
            it?.let {
                findNavController().navigate(
                    MenuFragmentDirections.navToMenu(it, "")
                )
            }
        })

        // Nav to AddShop
        viewModel.navToAddShop.observe(viewLifecycleOwner, {
            it?.let { findNavController().navigate(HomeFragmentDirections.navToAddShop()) }
        })

        // Nav to Setting
        viewModel.navToSetting.observe(viewLifecycleOwner, {
            it?.let {
                findNavController().navigate(
                    HomeFragmentDirections.navToSetting(
                        viewModel.shopListLiveData.value!!.toTypedArray()
                    )
                )
            }
        })

        // Observe ShopData
        viewModel.shopListLiveData.observe(viewLifecycleOwner, { shopData ->

            allShopData = shopData

            // Observe when get currentPosition
            viewModel.trafficMode.value?.let { mode ->
                mainViewModel.currentFragmentType.value?.let { getDirection(mode, it) }
            }

            // Observe mainViewModel shopFilteredList from firebase
            mainViewModel.firebaseFilteredShopList.observe(viewLifecycleOwner, { dbList ->

                // Set Search bar
                setSearchBar(dbList)

                // AddMarker when user isSearch for something in search bar
                if (queryShopName.isEmpty()) {
                    addMarkerAfterClearMap(dbList)
                } else {
                    addMarkerAfterClearMap(allShopName.filter { it != queryShopName })
                }
            })
        })

        // Import allShopName from viewModel
        viewModel.allShopName.observe(viewLifecycleOwner, {
            allShopName = it
        })

        // Observe getDirectionDone
        viewModel.getDirectionDone.observe(viewLifecycleOwner, {
            if (mainViewModel.currentFragmentType.value == CurrentFragmentType.ORDER_DETAIL) {
                it?.let {
                    viewModel.startDrawPolyLine()
                    viewModel.getDirectionDone.value = null
                }
            }
        })

        // Observe move camera
        viewModel.isMoveCamera.observe(viewLifecycleOwner, {
            it?.let {
                moveCameraToCurrentLocation(
                    18f, GoogleCameraMoveMode.ANIMATE
                )
            }
        })

        // Observe userSetting traffic time
        viewModel.userSettingTime.observe(viewLifecycleOwner, {
            it?.let { UserInfo.userCurrentSettingTrafficTime = it }
        })

        // Observe get google direction done
        viewModel.options.observe(viewLifecycleOwner, {

            // Clear polyLine
            if (this::polyLine.isInitialized) {
                polyLine.remove()
            }

            // Draw polyLine
            if (mainViewModel.currentFragmentType.value == CurrentFragmentType.HOME_DIALOG ||
                mainViewModel.currentFragmentType.value == CurrentFragmentType.ORDER_DETAIL
            ) {
                it?.let {
                    changeBtsBehaviorToNavigation()
                    moveCameraToCurrentLocation(17f, GoogleCameraMoveMode.ANIMATE)
                    polyLine = mMap.addPolyline(it)
                }
            }
        })

        // Observe trafficMode change for getDirection from walk or driving
        viewModel.trafficMode.observe(viewLifecycleOwner, {

            if (appPermission.locationPermissionGranted) {
                Logger.d("mode observe $it")

                MyAnimation.startSearchAnimation(
                    UserInfo.userCurrentLocation,
                    mMap,
                    vAnimator,
                    viewModel.distance
                )

                viewModel.getShopData(UserInfo.userCurrentLocation, "search")

                it?.let {
                    mainViewModel.currentFragmentType.value?.let { currentFragmentType ->
                        getDirection(it, currentFragmentType)
                    }
                }
            }
        })

        // Observe User Search Product
        viewModel.userSearchingProduct.observe(viewLifecycleOwner, { product ->
            startSearchingNearShops(product)
        })

        // Traffic time editText action_done
        binding.trafficTimeEdt.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == IME_ACTION_DONE) {
                MyAnimation.startSearchAnimation(
                    UserInfo.userCurrentLocation,
                    mMap,
                    vAnimator,
                    viewModel.distance
                )
                viewModel.getShopData(UserInfo.userCurrentLocation, "search")
            }
            false
        }

        viewModel.isSearchBarFocus.observe(viewLifecycleOwner, {
            if (it == false) {
                binding.searchView.clearFocus()
            }
        })
        return binding.root
    }


    // Add Marker On Map
    private fun addMarkerAfterClearMap(
        dbList: List<String>,
    ) {
        mMap.clear()
        allShopData.forEach { shop ->
            if (!dbList.contains(shop.name)) {
                val newPosition = LatLng(shop.lat, shop.lon)
                mMap.addMarker(
                    MarkerOptions().position(newPosition).snippet(shop.shop_Id)
                        .icon(
                            BitmapDescriptorFactory.fromBitmap(
                                setLabelOnMapMarker(
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
        vAnimator.pause()
    }

    // Set backPressed behavior
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

    // Stop Map navigation
    private fun stopMapNavigation() {
        bottomSheetNavBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        mainViewModel.currentFragmentType.value = CurrentFragmentType.HOME
        viewModel.currentFragmentType.value = CurrentFragmentType.HOME
        if (this::polyLine.isInitialized) {
            polyLine.remove()
        }
    }

    // On map ready
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        appPermission.askPermissionToGetDeviceLocation(this)
        setBottomSheetBehavior()
        moveCameraToSelectedShop()
    }

    // When nav from favorite move camera
    private fun moveCameraToSelectedShop() {
        if (this::selectedShop.isInitialized &&
            mainViewModel.currentFragmentType.value == CurrentFragmentType.FAVORITE
        ) {
            val position = LatLng(selectedShop.lat, selectedShop.lon)
            mMap.moveCamera(position, 20f, GoogleCameraMoveMode.IMMEDIATELY)
        }
    }

    // Set searchBar
    private fun setSearchBar(dbList: List<String>) {

        val listAdapter = SearchAdapter(viewModel)

        // Observe allProduct then submit to searchBar recycleView
        viewModel.allProduct.observe(viewLifecycleOwner, { list ->
            allProductList = list
            listAdapter.submitList(list)
        })

        // Detect searchView focus
        binding.searchView.setOnQueryTextFocusChangeListener { _, b ->
            viewModel.isSearchBarFocus.value = b
        }

        // Set QueryTextListener
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (filteredProudctList.isNotEmpty()) {
                    startSearchingNearShops(filteredProudctList.first())
                } else {
                    myToast("未搜尋到${binding.searchView.query}")
                    viewModel.isSearchBarFocus.value = false
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { text ->

                    filteredProudctList = allProductList.filter { it.type.contains(text) }
                    listAdapter.submitList(filteredProudctList)

                    if (filteredProudctList.isEmpty()) {
                        filteredProudctList = allProductList.filter { it.shop_Name.contains(text) }
                        listAdapter.submitList(filteredProudctList)
                    }

                    if (filteredProudctList.isEmpty()) {
                        filteredProudctList = allProductList.filter { it.name.contains(text) }
                        listAdapter.submitList(filteredProudctList)
                    }

                }
                return false
            }
        })

        // Get ClearBtn from searchView
        val clearButton: ImageView =
            binding.searchView.findViewById(androidx.appcompat.R.id.search_close_btn)

        clearButton.setOnClickListener {
            if (binding.searchView.query.isEmpty()) {
                binding.searchView.isIconified = true
            } else {
                addMarkerAfterClearMap(dbList)
                queryShopName = ""
                binding.searchView.setQuery("", false)
            }
        }
        binding.searchRev.adapter = listAdapter
    }

    // start Search
    private fun startSearchingNearShops(product: Product) {

        MyAnimation.startSearchAnimation(
            UserInfo.userCurrentLocation,
            mMap,
            vAnimator,
            viewModel.distance
        )

        queryShopName = product.shop_Name.last()
        binding.searchView.setQuery(product.name, false)
        myToast("正在搜尋附近含有 - ${product.name} 的店家 ")

        if (!allShopName.isNullOrEmpty()) {
            val searchName = allShopName.filter { it != queryShopName }
            addMarkerAfterClearMap(searchName)
        }

        binding.searchView.clearFocus()
        binding.searchRev.visibility(0)
    }

    // Set google map UI
    @SuppressLint("MissingPermission")
    fun setMapUI() {

        viewModel.getShopData(UserInfo.userCurrentLocation)

        if (!::selectedShop.isInitialized) {
            moveCameraToCurrentLocation(
                18f, GoogleCameraMoveMode.IMMEDIATELY
            )
        }

        mMap.uiSettings.isMyLocationButtonEnabled = false
        mMap.isMyLocationEnabled = true

        // Marker onClick
        mMap.setOnMarkerClickListener {

            if (mainViewModel.currentFragmentType.value != CurrentFragmentType.HOME_NAV) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                mainViewModel.currentFragmentType.value = CurrentFragmentType.HOME_DIALOG
                viewModel.currentFragmentType.value = CurrentFragmentType.HOME_DIALOG
                viewModel.getSelectedShopSnippet(it.snippet)

                getDirection(
                    "${viewModel.trafficMode.value}",
                    mainViewModel.currentFragmentType.value!!
                )

            }
            return@setOnMarkerClickListener true
        }

        // Map onClick
        mMap.setOnMapClickListener {
            if (mainViewModel.currentFragmentType.value != CurrentFragmentType.HOME_NAV) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        // Map camera move listener
        mMap.setOnCameraMoveListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.halfExpandedRatio = 0.14f
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            }
        }
    }

    // GetDirectionApi
    private fun getDirection(mode: String, currentFragmentType: CurrentFragmentType) {

        Logger.i(appPermission.locationPermissionGranted.toString())

        if (appPermission.locationPermissionGranted && this::selectedShop.isInitialized) {
            if (currentFragmentType == CurrentFragmentType.HOME_DIALOG ||
                currentFragmentType == CurrentFragmentType.ORDER_DETAIL
            ) {
                val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                        "origin=" + UserInfo.userCurrentLat + "," + UserInfo.userCurrentLng +
                        "&destination=" + selectedShop.lat + "," + selectedShop.lon +
                        "&mode=" + mode +
                        "&key=" + DIRECTION_API_KEY +
                        "&language=zh-TW"

                viewModel.getDirection(url)
            }

        } else {
            Logger.e(getString(R.string.getDirectionFail))
        }
    }

    // Move camera to user current location
    private fun moveCameraToCurrentLocation(
        zoomFloat: Float,
        googleCameraMoveMode: GoogleCameraMoveMode,
    ) {
        when (googleCameraMoveMode) {
            GoogleCameraMoveMode.ANIMATE -> mMap.moveCamera(
                UserInfo.userCurrentLocation,
                zoomFloat,
                googleCameraMoveMode
            )
            GoogleCameraMoveMode.IMMEDIATELY -> mMap.moveCamera(
                UserInfo.userCurrentLocation,
                zoomFloat,
                googleCameraMoveMode
            )
        }
    }

    // Make custom map marker
    private fun setLabelOnMapMarker(label: String, branch: String): Bitmap {
        val iconGenerator = IconGenerator(mContext)
        val markerView: View = LayoutInflater.from(mContext).inflate(R.layout.map_marker, null)
        val imgMarker = markerView.findViewById<ImageView>(R.id.mapIcon)
        val tvLabel = markerView.findViewById<TextView>(R.id.marker_shop)
        val tvLabel2 = markerView.findViewById<TextView>(R.id.marker_branch_name)
        imgMarker.setImageResource(R.drawable.location_brown2)
        tvLabel.text = label
        tvLabel2.text = branch
        iconGenerator.setContentView(markerView)
        iconGenerator.setBackground(null)
        return iconGenerator.makeIcon(label)
    }

    // Change bottomSheet behavior to navigation state
    private fun changeBtsBehaviorToNavigation() {
        bottomSheetNavBehavior.isDraggable = false
        bottomSheetBehavior.halfExpandedRatio = 0.0001f
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        mainViewModel.currentFragmentType.value = CurrentFragmentType.HOME_NAV
        viewModel.currentFragmentType.value = CurrentFragmentType.HOME_NAV
        bottomSheetNavBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    // Set bottomSheet behavior
    private fun setBottomSheetBehavior() {

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        //observe current fragment type
        viewModel.currentFragmentType.observe(viewLifecycleOwner, {
            when (it) {
                CurrentFragmentType.HOME ->
                    if (viewModel.status.value != LoadApiStatus.LOADING &&
                        binding.toolbarConstraint.visibility != View.VISIBLE
                    ) {
                        toolbarVisible()
                    }
                CurrentFragmentType.HOME_NAV -> toolbarGone()
                else -> {
                }
            }
        })

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

                if (slideOffset > 0.4f && x == 0) {
                    x = 1; toolbarGone()
                }

                if (x == 1 && slideOffset < 0.4f &&
                    viewModel.currentFragmentType.value != CurrentFragmentType.HOME_NAV
                ) {
                    x = 0; toolbarVisible()
                }
            }
        })
    }

    // Set toolbar visibility
    private fun toolbarVisible() {
        binding.toolbarConstraint.startAnimation(MyAnimation.fromTopVisibleAnimation)
        binding.toolbarConstraint.visibility(1)
    }

    // Set toolbar visibility
    private fun toolbarGone() {
        binding.toolbarConstraint.startAnimation(MyAnimation.toTopGoneAnimation)
        binding.toolbarConstraint.visibility(0)
    }
}





