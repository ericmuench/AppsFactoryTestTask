package de.ericmuench.appsfactorytesttask.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
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
        setupNavigation(viewBinding)
    }

    private fun setupNavigation(binding: ActivityMainBinding) {
        val navControl = findNavController(R.id.nav_host_fragment_container_main)
        binding.bottomNavigationMain.setupWithNavController(navControl)
    }

}