package com.tsai.shakeit

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.google.android.libraries.places.api.Places
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.tsai.shakeit.BuildConfig.MAPS_API_KEY
import com.tsai.shakeit.databinding.ActivityMainBinding
import com.tsai.shakeit.ext.getVmFactory
import com.tsai.shakeit.service.MyFirebaseService
import com.tsai.shakeit.ui.favorite.FavoriteFragmentDirections
import com.tsai.shakeit.ui.home.HomeFragmentDirections
import com.tsai.shakeit.ui.order.OrderFragmentDirections
import com.tsai.shakeit.util.CurrentFragmentType
import com.tsai.shakeit.util.Logger
import com.tsai.shakeit.util.UserInfo


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val viewModel by viewModels<MainViewModel> { getVmFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(/*context=*/this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            SafetyNetAppCheckProviderFactory.getInstance()
        )

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val navView = binding.bottomNavigation

        setUpBottomNavigation(navView, navController)

        viewModel.currentFragmentType.observe(this,{
            Logger.d("current fragment type = $it")
        })

        viewModel.shopFilterList.observe(this, {
            viewModel.updateFilterShopList(it)
        })

        // Initialize the SDK
        Places.initialize(applicationContext, MAPS_API_KEY)

        // Create a new PlacesClient instance
        val placesClient = Places.createClient(this)

        setupNavController(navView)
    }

    private fun setUpBottomNavigation(
        navView: MeowBottomNavigation,
        navController: NavController
    ) {
        navView.add(MeowBottomNavigation.Model(1, R.drawable.homeicon))
        navView.add(MeowBottomNavigation.Model(2, R.drawable.heartb))
        navView.add(MeowBottomNavigation.Model(3, R.drawable.shoppinglist))
        navView.setOnClickMenuListener {
            when (it.id) {
                1 -> navController.navigate(HomeFragmentDirections.navToHome())
                2 -> navController.navigate(FavoriteFragmentDirections.navToFavorite())
                3 -> navController.navigate(OrderFragmentDirections.navToOrder())
            }
        }
    }

    private fun setupNavController(navView: MeowBottomNavigation) {
        findNavController(R.id.nav_host_fragment_activity_main).addOnDestinationChangedListener { navController: NavController, _: NavDestination, _: Bundle? ->
            viewModel.currentFragmentType.value = when (navController.currentDestination?.id) {

                R.id.loginFragment -> CurrentFragmentType.LOGIN

                R.id.navigation_home -> {
                    navView.show(1, true)
                    return@addOnDestinationChangedListener
                }

                R.id.menuFragment ->  CurrentFragmentType.MENU
                R.id.addShopFragment -> CurrentFragmentType.ADD_SHOP
                R.id.drinksDetailFragment -> CurrentFragmentType.DRINKS_DETAIL
                R.id.orderDetailFragment -> CurrentFragmentType.ORDER_DETAIL

                R.id.navigation_order -> {
                    navView.show(3, true)
                    CurrentFragmentType.ORDER
                }

                R.id.navigation_favorite -> {
                    navView.show(2, true)
                    CurrentFragmentType.FAVORITE
                }

                R.id.orderHistoryFragment ->{
                    CurrentFragmentType.ORDER_HISTORY
                }

                else -> viewModel.currentFragmentType.value
            }
        }
    }
}

