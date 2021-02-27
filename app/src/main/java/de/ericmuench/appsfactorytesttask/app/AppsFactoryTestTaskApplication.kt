package de.ericmuench.appsfactorytesttask.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import de.ericmuench.appsfactorytesttask.model.repository.DataRepository
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

        //TODO(In future releases this could be changed to a setting so that users not
        // running Android 10 can decide on their own if they want Light- or DarkMode.
        // For now, the Night-Mode is always activated because it looks more smooth and
        // not as bright and dazzling as the Light Mode)
        val nightModeOption = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        else{
            AppCompatDelegate.MODE_NIGHT_YES
        }

        AppCompatDelegate.setDefaultNightMode(nightModeOption)

    }
}