package de.ericmuench.appsfactorytesttask.util.connectivity

import android.content.Context
import android.net.*
import androidx.lifecycle.*
import de.ericmuench.appsfactorytesttask.util.extensions.connectivityManager
import de.ericmuench.appsfactorytesttask.util.extensions.notNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.lang.ref.WeakReference
import java.net.InetSocketAddress
import java.net.Socket

/**
 * This class can check if the device is connected to Internet. For this it can be registered to a
 * lifecycle as a Lifecycle-Observer registering a Network-Callback in OnCreate and OnDestroy. When
 * the Network-State changes, it publishes the current State of the Network into the
 * "internetConnectivityState"-Variable of Type InternetConnectivityState. The latter represents
 * the current Internet-Connection-State.
 * */
class InternetConnectivityChecker(
    context: Context
) : ConnectivityManager.NetworkCallback(), DefaultLifecycleObserver {

    //region Fields
    private val contextRef = WeakReference(context.applicationContext)
    var internetConnectivityState : InternetConnectivityState = InternetConnectivityState.UNDETERMINED
        private set

    //endregion

    //region Lifecycle-Observer Functions
    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        try {
            val context = contextRef.get()
            context?.connectivityManager.notNull {
                val request = NetworkRequest
                    .Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build()
                it.registerNetworkCallback(request,this)
            }
        }
        catch(ex : Exception){
            ex.printStackTrace()
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        try {
            val context = contextRef.get()
            context?.connectivityManager.notNull {
                it.unregisterNetworkCallback(this)
            }
        }
        catch (ex: Exception){
            ex.printStackTrace()
        }
    }
    //endregion

    //region Overridden functions from Superclasses
    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        internetConnectivityState = InternetConnectivityState.CONNECTED
    }

    override fun onLosing(network: Network, maxMsToLive: Int) {
        super.onLosing(network, maxMsToLive)
        internetConnectivityState = InternetConnectivityState.LOSING
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        internetConnectivityState = InternetConnectivityState.LOST
    }

    override fun onUnavailable() {
        super.onUnavailable()
        internetConnectivityState = InternetConnectivityState.DISCONNECTED
    }
    //endregion

    //region Enum for Connection State
    enum class InternetConnectivityState(val hasInternetConnection : Boolean){
        CONNECTED(true),
        LOSING(true),
        LOST(false),
        DISCONNECTED(false),
        UNDETERMINED(true) //If Connectivity-State is
                                           // indetermite, lets give it a try and see if there is
                                           // internet
    }
    //endregion
}