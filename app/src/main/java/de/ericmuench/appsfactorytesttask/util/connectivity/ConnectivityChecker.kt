package de.ericmuench.appsfactorytesttask.util.connectivity

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.net.InetAddress

/**
 * This class can check if the device is connected in a certain way
 * */
class ConnectivityChecker(cntxt: Context?){

    private val context : Context? = cntxt?.applicationContext

    //Functions
    /**
     * This class can check if the device is connected to internet
     *
     * This check is NOT made via ConnectivityManager due to the fact that there is a state where
     * the device is connected to a network but does not have internet access. Instead, a simple
     * check of reaching google is used.
     * --> https://stackoverflow.com/questions/9570237/android-check-internet-connection
     *
     *
     * TODO: Maybe change to the Network-Callback-check later if there is time left
     * */
    suspend fun isConnectedToInternet() : Boolean = coroutineScope{
        return@coroutineScope withContext(Dispatchers.IO) {
            try {
                val ipAddr: InetAddress = InetAddress.getByName("google.com")
                !ipAddr.equals("")
            } catch (e: Exception) {
                false
            }
        }

    }
}