package de.ericmuench.appsfactorytesttask.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import de.ericmuench.appsfactorytesttask.R
import de.ericmuench.appsfactorytesttask.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    //lifecycle functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //viewbinding
        val viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        //setup Layout
        val navController = findNavController(R.id.nav_host_fragment_container_main)
        setupToolbar(viewBinding,navController)
        setupNavigation(viewBinding, navController)
    }

    //help functions for layout setup
    private fun setupToolbar(binding: ActivityMainBinding, navControl : NavController) = with(binding){
        setSupportActionBar(toolbarMain.root)
        val appBarConfig = AppBarConfiguration(
            setOf(
                R.id.albumsOverviewFragment,
                R.id.searchArtistFragment
            )
        )
        setupActionBarWithNavController(navControl,appBarConfig)
    }

    private fun setupNavigation(binding: ActivityMainBinding, navControl : NavController)
        = binding.bottomNavigationMain.setupWithNavController(navControl)

}