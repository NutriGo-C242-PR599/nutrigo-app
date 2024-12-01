package com.nutrigo.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nutrigo.R
import com.nutrigo.databinding.ActivityMainBinding
import com.nutrigo.ui.contribute.ContributeFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

class MainActivity : AppCompatActivity() {

    private val splashScope = CoroutineScope(Dispatchers.Main)
    private var isSplashScreenVisible= true

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(ContributeFragment.PRODUCT_CODE)){
            val productCode = intent.getStringExtra(ContributeFragment.PRODUCT_CODE)

            showContributeFragment(productCode)
        }

        splashScreen.setKeepOnScreenCondition {
            isSplashScreenVisible
        }

        Handler(Looper.getMainLooper()).postDelayed({
            isSplashScreenVisible = false
        }, 2000)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setupWithNavController(navController)


    }

    private fun showContributeFragment(productCode: String?) {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val bundle = Bundle().apply {
            putString(ContributeFragment.PRODUCT_CODE, productCode)
        }
        navController.navigate(R.id.navigation_contribute, bundle)
    }



    override fun onDestroy() {
        super.onDestroy()
        splashScope.cancel()
    }
}