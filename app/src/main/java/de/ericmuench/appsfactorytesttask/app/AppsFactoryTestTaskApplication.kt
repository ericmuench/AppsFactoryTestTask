package de.ericmuench.appsfactorytesttask.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import de.ericmuench.appsfactorytesttask.model.room.AppDatabase
import de.ericmuench.appsfactorytesttask.model.runtime.repository.DataRepository
import de.ericmuench.appsfactorytesttask.util.connectivity.InternetConnectivityChecker

class AppsFactoryTestTaskApplication : Application() {

    companion object{
        /**
         * This field will get Track of the Internet-Connection State and can be used by Activities
         * to register it for their lifecycles.
         * */
        lateinit var internetConnectivityChecker : InternetConnectivityChecker
    }

    lateinit var dataRepository : DataRepository
        private set

    override fun onCreate() {
        super.onCreate()
        dataRepository = DataRepository(this)
        internetConnectivityChecker = InternetConnectivityChecker(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
}