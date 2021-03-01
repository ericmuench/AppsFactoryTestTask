package de.ericmuench.appsfactorytesttask.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import de.ericmuench.appsfactorytesttask.model.repository.DataRepository
import de.ericmuench.appsfactorytesttask.util.connectivity.InternetConnectivityChecker

class AppsFactoryTestTaskApplication : Application() {

    /**
     * This field defines a Repository for the Data of ths app
     * */
    lateinit var dataRepository : DataRepository
        private set

    /**
     * This field will get Track of the Internet-Connection State and can be used by Activities
     * to register it for their lifecycles.
     * */
    lateinit var internetConnectivityChecker : InternetConnectivityChecker
        private set

    override fun onCreate() {
        super.onCreate()
        dataRepository = DataRepository(this)
        internetConnectivityChecker = InternetConnectivityChecker(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

    }
}