package de.ericmuench.appsfactorytesttask.util.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import de.ericmuench.appsfactorytesttask.util.extensions.connectivityManager
import de.ericmuench.appsfactorytesttask.util.extensions.notNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import java.net.InetSocketAddress
import java.net.Socket

/**
 * This class can check if the device is connected to Internet TODO : More Doc
 * */
class InternetConnectivityChecker(
    context: Context
) : ConnectivityManager.NetworkCallback(), LifecycleObserver{

    //private val contextRef : WeakReference<Context> = WeakReference(context.applicationContext)
    //region Fields
    private val contextRef = WeakReference(context)
    var internetConnectivityState : InternetConnectivityState = InternetConnectivityState.UNDETERMINED
        private set

    //endregion

    //region Functions
    /**
     * This function can check if the device is connected to internet. For that, ConnectivityManager
     * is used.
     *
     * @param defaultForIndeterminable The default value that should be returned if Internet-
     * Connection-State can not be determined.
     *
     * @return False if ConnectivityManager detects that Internet-Connection is not available,
     * True if ConnectivityManager detects that Internet-Connection is available and
     * defaultForIndeterminable if the Internet-Connection-State can not be determined.
     *
     * TODO: Maybe change to the Network-Callback-check later if there is time left
     * */
    @Deprecated("This function should not be used anymore due to its inefficency. " +
            "Use internetConnectivityState instead")
    suspend fun isConnectedToInternet(
        defaultForIndeterminable : Boolean = true
    ) : Boolean = coroutineScope{
        return@coroutineScope withContext(Dispatchers.IO) {
            try {
                val socket = Socket()
                socket.connect(InetSocketAddress("8.8.8.8",53),1500)
                socket.close()
                true
            } catch (e: Exception) {
                false
            }

        }
    }
    //endregion

    //region Lifecycle-Observer Functions
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun registerNetworkListener(){
        contextRef.get()?.connectivityManager.notNull {
            val request = NetworkRequest
                .Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            it.registerNetworkCallback(request,this)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun unregisterNetworkListener(){
        contextRef.get()?.connectivityManager.notNull {
            it.unregisterNetworkCallback(this)
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
        UNDETERMINED(false)
    }
    //endregion
}