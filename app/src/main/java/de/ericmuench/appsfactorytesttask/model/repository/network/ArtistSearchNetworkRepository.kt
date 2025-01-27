package de.ericmuench.appsfactorytesttask.model.repository.network

import android.content.Context
import com.github.kittinunf.result.Result
import de.ericmuench.appsfactorytesttask.R
import de.ericmuench.appsfactorytesttask.clerk.network.LastFmApiClient
import de.ericmuench.appsfactorytesttask.model.runtime.ArtistSearchResult
import de.ericmuench.appsfactorytesttask.model.repository.util.DataRepositoryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * This class defines a repository for providing all data associated with Searching an Artist
 * */

class ArtistSearchNetworkRepository(
    apiClient : LastFmApiClient,
    context : Context
) : NetworkRepository(apiClient,context){
    //region fields
    /**This field caches the current Search-Query results and search states*/
    private val artistSearchCache = ArtistSearchCache()

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
        }
    //endregion

    //region functions for artist search
    suspend fun searchForArtists(
        hasInternet : Boolean,
        searchQuery : String,
        startPage : Int,
        limitPerPage : Int
    ) : DataRepositoryResponse<ArtistSearchResult, Throwable> = coroutineScope{

        val cachedDataDef = async(Dispatchers.IO){ artistSearchCache.getData(searchQuery, startPage, limitPerPage) }
        val cachedData = cachedDataDef.await()
        if(cachedData != null){
            //There is data available in the cache that is valid
            return@coroutineScope DataRepositoryResponse.Data(cachedData)
        }

        //There is no cached data available -> load data from network
        if(!hasInternet){
            return@coroutineScope DataRepositoryResponse.Error(
                createThrowable(R.string.no_internet_connection)
            )
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

    suspend fun lastArtistSearchResult() : DataRepositoryResponse<List<ArtistSearchResult>, Throwable>
            = coroutineScope{
        val cachedDef = async(Dispatchers.IO){ artistSearchCache.lastSearchResult() }
        return@coroutineScope DataRepositoryResponse.Data(cachedDef.await())
    }
    //endregion
}