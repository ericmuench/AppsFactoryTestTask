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
    /**
     * This function sets up the Toolbar of this activity.
     * @param binding The Binding for this class to access layout components
     * @param navControl The NavController to link the action bar title with the current fragment
     */
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

    /**
     * This functions sets up the Navigation via BottomNavigation
     * @param binding The Binding for this class to access layout components
     * @param navControl The NavController to be linked with the BottomNavigation
     */
    private fun setupNavigation(binding: ActivityMainBinding, navControl : NavController)
        = binding.bottomNavigationMain.setupWithNavController(navControl)

}