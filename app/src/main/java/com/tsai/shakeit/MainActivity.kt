package com.tsai.shakeit

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.google.android.libraries.places.api.Places
import com.tsai.shakeit.BuildConfig.MAPS_API_KEY
import com.tsai.shakeit.databinding.ActivityMainBinding
import com.tsai.shakeit.ext.getVmFactory
import com.tsai.shakeit.ui.favorite.FavoriteFragmentDirections
import com.tsai.shakeit.ui.home.HomeFragmentDirections
import com.tsai.shakeit.ui.menu.MenuFragmentDirections
import com.tsai.shakeit.ui.order.OrderFragmentDirections
import com.tsai.shakeit.util.CurrentFragmentType
import com.tsai.shakeit.util.Logger


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val viewModel by viewModels<MainViewModel> { getVmFactory() }


    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val navView = binding.bottomNavigation

        navView.add(MeowBottomNavigation.Model(1, R.drawable.ic_home_black_24dp))
        navView.add(MeowBottomNavigation.Model(2, R.drawable.ic_dashboard_black_24dp))
        navView.add(MeowBottomNavigation.Model(3, R.drawable.ic_notifications_black_24dp))

        navView.setOnClickMenuListener {
            when (it.id) {
                1 -> navController.navigate(HomeFragmentDirections.navToHome())
                2 -> navController.navigate(FavoriteFragmentDirections.navToFavorite())
                3 -> navController.navigate(OrderFragmentDirections.navToOrder())
            }
        }

        viewModel.getFilterList()

        viewModel.dbFilterShopList.observe(this, {
            Logger.d("it =$it")
        })

        viewModel.currentFragmentType.observe(
            this,
            Observer {
                Logger.i("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
                Logger.i("[${viewModel.currentFragmentType.value}]")
                Logger.i("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
            }
        )

        viewModel.shopFilterList.observe(this, Observer {
            viewModel.updateFilterShopList(it)
        })

        // Initialize the SDK
        Places.initialize(applicationContext, MAPS_API_KEY)

        // Create a new PlacesClient instance
        val placesClient = Places.createClient(this)


        setupNavController()
//        navView.setupWithNavController(navController)
    }

    private fun setupNavController() {
        findNavController(R.id.nav_host_fragment_activity_main).addOnDestinationChangedListener { navController: NavController, _: NavDestination, _: Bundle? ->
            viewModel.currentFragmentType.value = when (navController.currentDestination?.id) {
//                R.id.navigation_home -> CurrentFragmentType.HOME
                R.id.menuFragment -> {
                    CurrentFragmentType.MENU
                }
                R.id.addShopFragment -> CurrentFragmentType.ADD_SHOP
                R.id.drinksDetailFragment -> CurrentFragmentType.DRINKS_DETAIL
                R.id.orderDetailFragment -> CurrentFragmentType.ORDER_DETAIL
                R.id.navigation_order -> CurrentFragmentType.ORDER
                R.id.navigation_favorite -> CurrentFragmentType.FAVORITE
                else -> viewModel.currentFragmentType.value
            }
        }
    }
}

