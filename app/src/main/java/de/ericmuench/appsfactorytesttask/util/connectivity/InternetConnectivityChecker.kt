package de.ericmuench.appsfactorytesttask.util.connectivity

import android.content.Context
import android.net.*
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import de.ericmuench.appsfactorytesttask.util.extensions.connectivityManager
import de.ericmuench.appsfactorytesttask.util.extensions.notNull
import kotlinx.coroutines.*
import java.io.IOException
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

    //region init
    init{
        initialConnectionCheck()
    }
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

    //region Help functions
    /**
     * This function does an initial internet connectivity check by trying to get a connection
     * to a Server (Google in this case). If the value of internetConnectivityState is still
     * UNDETERMINED after the test has finished it will assign the corresponding InternetConnectivityState
     * to the field.
     *
     * The code used for the test is based on the following tutorial:
     * https://www.youtube.com/watch?v=OEclG3XsPsg
     * */
    private fun initialConnectionCheck() = GlobalScope.launch{
        val connectStateDef = async(Dispatchers.IO){
            try {
                val socket = Socket()
                socket.connect(InetSocketAddress("8.8.8.8",53),1500)
                socket.close()
                return@async InternetConnectivityState.CONNECTED
            }
            catch(ex: IOException){
                return@async InternetConnectivityState.DISCONNECTED
            }
        }

        val connectionState = connectStateDef.await()
        if(internetConnectivityState == InternetConnectivityState.UNDETERMINED){
            internetConnectivityState = connectionState
        }
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