package com.tsai.shakeit

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.tsai.shakeit.databinding.ActivityMainBinding
import com.tsai.shakeit.ui.home.HomeDialogFragment


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )

//        setupActionBarWithNavController(navController, appBarConfiguration)
        setupNavController()
        navView.setupWithNavController(navController)
    }

    private fun setupNavController() {
        findNavController(R.id.nav_host_fragment_activity_main).addOnDestinationChangedListener { navController: NavController, _: NavDestination, _: Bundle? ->
            when (navController.currentDestination?.id) {
                R.id.menuFragment -> binding.navView.visibility = View.GONE
                R.id.drinksDetailFragment -> binding.navView.visibility = View.GONE
                else -> binding.navView.visibility = View.VISIBLE

            }
        }
    }
}

