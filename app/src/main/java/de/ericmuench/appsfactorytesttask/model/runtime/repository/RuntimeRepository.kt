package de.ericmuench.appsfactorytesttask.model.runtime.repository

import com.github.kittinunf.result.Result
import de.ericmuench.appsfactorytesttask.clerk.network.LastFmApiClient
import de.ericmuench.appsfactorytesttask.model.runtime.Album
import de.ericmuench.appsfactorytesttask.model.runtime.Artist
import de.ericmuench.appsfactorytesttask.util.connectivity.ConnectivityChecker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.io.IOException

/**
 * This class defines the Repository for managing artists,Albums and Songs. It was
 * implemented to separately manage Artists, Songs and Albums stored at Runtime from those used for
 * the Search as well as for the Database.
 * */
class RuntimeRepository(private val apiClient: LastFmApiClient){

    //region fields
    /**
     * This Map stores the mbid of an artist associated with an Artist at runtime.
     * */
    private val artistRuntimeStorage = mutableMapOf<String, Artist>()
    /**
     * This Map stores the mbid of an artist associated associated with its albums at runtime.
     * */
    private val albumRuntimeStorage = mutableMapOf<String, List<Album>>()
    //endregion

    //region functions
    /**
     * This function loads information about an Artist into the Runtime-Storages from the Internet
     * (as long as the Artist isn't already in the Runtime-Storages).
     * This loading is based on the mbid of the Artist.
     *
     * @param mbid The mbid of the Artist to uniquely identify the Artist
     * @param shouldIgnoreRuntimeCache Whether the current data in the Runtime-Storage should be
     * ignored and can be overridden
     *
     * @return A DataRepositoryResult containing the Artist with all its Properties or an Error
     * */
    suspend fun getArtistByMbid(
        connectivityChecker: ConnectivityChecker,
        mbid: String,
        shouldIgnoreRuntimeCache : Boolean = false
    ) : DataRepositoryResponse<Artist,Throwable> = coroutineScope{
        if(!shouldIgnoreRuntimeCache){
            //Do not ignore cache and get Data from it
            val dataFromStorage = artistRuntimeStorage[mbid]

            if(dataFromStorage != null){
                return@coroutineScope DataRepositoryResponse.Data(dataFromStorage)
            }
        }
        //Load data from web and store it into storage if internet connection is available
        if(!hasInternetConnection(connectivityChecker)){
            return@coroutineScope DataRepositoryResponse.Error(IOException("No internet Connection"))
        }

        val artistDataFromWebDeferred = async(Dispatchers.IO){ apiClient.getArtist(mbid) }

        when(val webResult = artistDataFromWebDeferred.await()){
            is Result.Success -> {
                artistRuntimeStorage[mbid] = webResult.value
                return@coroutineScope DataRepositoryResponse.Data(webResult.value)
            }
            is Result.Failure -> return@coroutineScope DataRepositoryResponse.Error(webResult.error)
        }
    }
    //endregion

    //region help functions
    private suspend fun hasInternetConnection(
        connectivityChecker: ConnectivityChecker
    ) : Boolean = coroutineScope{
        val hasInternetConnectionDef = async { connectivityChecker.isConnectedToInternet() }
        return@coroutineScope hasInternetConnectionDef.await()
    }
    //endregion
}