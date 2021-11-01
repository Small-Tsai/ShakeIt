package com.tsai.shakeit

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tsai.shakeit.databinding.ActivityMainBinding
import com.tsai.shakeit.ext.getVmFactory
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

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        viewModel.getFilterList()

        viewModel.dbFilterShopList.observe(this,{
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



//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_home, R.id.navigation_favorite, R.id.navigation_order
//            )
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)

        setupNavController()
        navView.setupWithNavController(navController)
    }

    private fun setupNavController() {
        findNavController(R.id.nav_host_fragment_activity_main).addOnDestinationChangedListener { navController: NavController, _: NavDestination, _: Bundle? ->
            viewModel.currentFragmentType.value = when (navController.currentDestination?.id) {
//                R.id.navigation_home -> CurrentFragmentType.HOME
                R.id.menuFragment -> { CurrentFragmentType.MENU }
                R.id.drinksDetailFragment ->  CurrentFragmentType.DRINKS_DETAIL
                R.id.orderDetailFragment -> CurrentFragmentType.ORDER_DETAIL
                R.id.navigation_order -> CurrentFragmentType.ORDER
                R.id.navigation_favorite -> CurrentFragmentType.FAVORITE
                else -> viewModel.currentFragmentType.value
            }
        }
    }
}

