package de.ericmuench.appsfactorytesttask.model.runtime.repository

import com.github.kittinunf.result.Result
import de.ericmuench.appsfactorytesttask.clerk.network.LastFmApiClient
import de.ericmuench.appsfactorytesttask.model.runtime.ArtistSearchCache
import de.ericmuench.appsfactorytesttask.model.runtime.ArtistSearchResult
import de.ericmuench.appsfactorytesttask.util.connectivity.ConnectivityChecker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * This object defines the global DataSource for the runtime Model. All Data is loaded from here.
 * It does abstraction over Network calls or local DB- data access so that higher architecture-
 * layer do not have to care about the origin of the data. Additionally to that, runtime-caching
 * is provided.
 */
object DataRepository {

    //region fields
    /**This field is responsible for all network API-Calls*/
    private val apiClient = LastFmApiClient()
    val artistSearchRepository = ArtistSearchRepository(apiClient)

    //TODO: Add further fields for room and runtime cache

    /**This field caches the current Search-Query results and search states*/
    /*private val artistSearchCache = ArtistSearchCache()

    var isSearchingArtist : Boolean
    get() = artistSearchCache.isSearching
    set(value) {
        artistSearchCache.isSearching = value
    }

    var artistSearchQuery : String
        get() = artistSearchCache.lastSearchQuery
        set(value) {
            artistSearchCache.lastSearchQuery = value
        }

    var pendingArtistSearchQuery : String
        get() = artistSearchCache.pendingQuery
        set(value) {
            artistSearchCache.pendingQuery = value
        }*/
    //endregion

    //region functions for artist search
    /*suspend fun searchForArtists(
        connectivityChecker: ConnectivityChecker,
        searchQuery : String,
        startPage : Int = 1,
        limitPerPage : Int = 10
    ) : DataRepositoryResponse<ArtistSearchResult,Throwable> = coroutineScope{

        val cachedDataDef = async(Dispatchers.IO){ artistSearchCache.getData(searchQuery, startPage, limitPerPage) }
        val cachedData = cachedDataDef.await()
        if(cachedData != null){
            //There is data available in the cache that is valid
            return@coroutineScope DataRepositoryResponse.Data(cachedData)
        }

        //There is no cached data available -> load data from network
        val hasInternetConnectionDef = async { connectivityChecker.isConnectedToInternet() }
        if(!hasInternetConnectionDef.await()){
            return@coroutineScope DataRepositoryResponse.Error(IOException("No Internet-Connection available"))
        }

        val resultDeferred = async(Dispatchers.IO) {
            apiClient.searchArtists(searchQuery,startPage,limitPerPage)
        }

        return@coroutineScope when(val result = resultDeferred.await()){
            is Result.Success -> {
                val distinctDataDef = async(Dispatchers.IO) {
                    artistSearchCache.eliminateDuplicates(searchQuery,result.value)
                }
                val distinctData = distinctDataDef.await()
                launch(Dispatchers.IO){
                    artistSearchCache.mergeData(searchQuery,startPage,limitPerPage,distinctData)
                }
                DataRepositoryResponse.Data(distinctData)
            }
            is Result.Failure -> DataRepositoryResponse.Error(result.error)
        }
    }

    suspend fun lastArtistSearchResult() : DataRepositoryResponse<List<ArtistSearchResult>,Throwable>
        = coroutineScope{
        val cachedDef = async(Dispatchers.IO){ artistSearchCache.lastSearchResult() }
        return@coroutineScope DataRepositoryResponse.Data(cachedDef.await())
    }*/
    //endregion

}
