package de.ericmuench.appsfactorytesttask.model.runtime.repository

import com.github.kittinunf.result.Result
import de.ericmuench.appsfactorytesttask.clerk.network.LastFmApiClient
import de.ericmuench.appsfactorytesttask.model.runtime.Artist
import de.ericmuench.appsfactorytesttask.model.runtime.TopAlbumOfArtistResult
import de.ericmuench.appsfactorytesttask.util.connectivity.ConnectivityChecker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * This class defines the Repository for managing artists,Albums and Songs. It was
 * implemented to separately manage Artists, Songs and Albums stored at Runtime from those used for
 * the Search as well as for the Database.
 * */
class RuntimeRepository(private val apiClient: LastFmApiClient){

    //region fields
    /**
     * This Map stores the name of an artist associated with an Artist at runtime.
     * Unfortunately, storing an Id as a key s not possible, given that LastFM does not always
     * deliver an mbid and never returns an Id.
     * */
    private val artistRuntimeStorage = mutableMapOf<String, Artist>()
    /**
     * This Map stores the name of an artist associated associated with its top-albums fetch results
     * at runtime. Unfortunately, storing an Id as a key s not possible, given that LastFM does not always
     * deliver an mbid and never returns an Id.
     * */
    private val topAlbumResultsRuntimeStorage = mutableMapOf<String, List<TopAlbumOfArtistResult>>()
    //endregion

    //region functions
    /**
     * This function loads information about an Artist into the Runtime-Storages from the Internet
     * (as long as the Artist isn't already in the Runtime-Storages).
     * This loading is based on the mbid of the Artist.
     *
     * @param connectivityChecker Checker-Class to check if Internet-Connectivity is available
     * @param name The name of the Artist to uniquely identify the Artist (Ids are not provided
     * by LastFM)
     * @param shouldIgnoreRuntimeCache Whether the current data in the Runtime-Storage should be
     * ignored and can be overridden
     *
     * @return A DataRepositoryResult containing the Artist with all its Properties or an Error
     * */
    suspend fun getArtistByName(
        connectivityChecker: ConnectivityChecker,
        name: String,
        shouldIgnoreRuntimeCache : Boolean = false
    ) : DataRepositoryResponse<Artist,Throwable> = coroutineScope{
        if(!shouldIgnoreRuntimeCache){
            //Do not ignore cache and get Data from it
            val dataFromStorage = artistRuntimeStorage[name]

            if(dataFromStorage != null){
                return@coroutineScope DataRepositoryResponse.Data(dataFromStorage)
            }
        }
        //Load data from web and store it into storage if internet connection is available
        if(!hasInternetConnection(connectivityChecker)){
            return@coroutineScope DataRepositoryResponse.Error(IOException("No internet Connection"))
        }

        val artistDataFromWebDeferred = async(Dispatchers.IO){ apiClient.getArtistByName(name) }

        when(val webResult = artistDataFromWebDeferred.await()){
            is Result.Success -> {
                artistRuntimeStorage[name] = webResult.value
                return@coroutineScope DataRepositoryResponse.Data(webResult.value)
            }
            is Result.Failure -> return@coroutineScope DataRepositoryResponse.Error(webResult.error)
        }
    }

    /**
     * This function loads a paged Result of Top-Albums of an artist. If the result is already
     * available in Cache, the data is loaded directly from it (unless the pageSize/limit per page
     * does not match --> all data associated with an artist is deleted in this case) or from the
     * web using the API-Client. If data is loaded from the Web it is also cached in the
     * associated Runtime-Storage for Top-Albums.
     *
     * @param connectivityChecker Checker-Class to check if Internet-Connectivity is available
     * @param artistName The name of the Artist to uniquely identify the Artist (Ids are not provided
     * by LastFM)
     * @param startPage The page to start with in the Result
     * @param start
     *
     *
     * */
    suspend fun getTopAlbumsByArtistName(
        connectivityChecker: ConnectivityChecker,
        artistName: String,
        startPage : Int,
        limitPerPage : Int,
    ): DataRepositoryResponse<TopAlbumOfArtistResult,Throwable> = coroutineScope{
        //looking for cached result
        val cachedResult = topAlbumResultsRuntimeStorage[artistName]
        if(cachedResult != null && cachedResult.isNotEmpty()){
            val cachedPageDef = async(Dispatchers.IO){ cachedResult.find { it.page == startPage } }
            val cachedPage = cachedPageDef.await()
            if(cachedPage != null){
                if(cachedPage.perPage != limitPerPage){
                    //delete data if page-size changed and data needs to be fetched again to keep
                    //the perPage-Value constant
                    topAlbumResultsRuntimeStorage.remove(artistName)
                }
                else{
                    return@coroutineScope DataRepositoryResponse.Data(cachedPage)
                }
            }
        }


        //Load data from web and store it into storage if internet connection is available
        if(!hasInternetConnection(connectivityChecker)){
            return@coroutineScope DataRepositoryResponse.Error(IOException("No internet Connection"))
        }

        val topAlbumsResultFromWebDeferred = async(Dispatchers.IO){
            apiClient.getTopAlbumsOfArtist(artistName, startPage, limitPerPage)
        }

        val topAlbumsFromWebResult = topAlbumsResultFromWebDeferred.await()
        return@coroutineScope when(topAlbumsFromWebResult){
            is Result.Success -> {
                val topAlbumsRes = topAlbumsFromWebResult.value
                launch(Dispatchers.IO){
                    if(topAlbumsRes.albums.isNotEmpty()){
                        addToTopAlbumResultRuntimeStorage(topAlbumsRes,artistName)
                    }
                }
                DataRepositoryResponse.Data(topAlbumsRes)
            }
            is Result.Failure -> {
                topAlbumsFromWebResult.error.printStackTrace()
                DataRepositoryResponse.Error(topAlbumsFromWebResult.error)
            }
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

    private suspend fun addToTopAlbumResultRuntimeStorage(
        topAlbumResult : TopAlbumOfArtistResult,
        artistName: String
    ) = coroutineScope{
        val topAlbumResFromRuntimeStorage = topAlbumResultsRuntimeStorage[artistName]

        if(topAlbumResFromRuntimeStorage == null){
            topAlbumResultsRuntimeStorage[artistName] = listOf(topAlbumResult)
        }
        else{
            topAlbumResultsRuntimeStorage[artistName] =
                topAlbumResFromRuntimeStorage
                    .toMutableList()
                    .apply {
                        add(topAlbumResult)
                    }
                    .sortedBy { it.page }
        }
    }
    //endregion
}