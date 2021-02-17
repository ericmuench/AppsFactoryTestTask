package de.ericmuench.appsfactorytesttask.model.runtime

import com.github.kittinunf.result.Result
import de.ericmuench.appsfactorytesttask.clerk.network.LastFmApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * This object defines the global DataSource for the runtime Model. All Data is loaded from here.
 * It does abstraction over Network calls or local DB- data access so that higher architecture-
 * layer do not have to care about the origin of the data. Additionally to that, runtime-caching
 * is provided.
 */
object DataRepository {
    //fields
    /**This field is responsible for all network API-Calls*/
    private val apiClient = LastFmApiClient()

    //TODO: Add further fields for room and runtime cache

    //functions
    suspend fun searchForArtists(
        searchQuery : String,
        startPage : Int = 1,
        limitPerPage : Int = 10
    ) : DataRepositoryResponse<ArtistSearchResults,Throwable> = coroutineScope{
        val resultDeferred = async(Dispatchers.IO) {
            apiClient.searchArtists(searchQuery,startPage,limitPerPage)
        }

        return@coroutineScope when(val result = resultDeferred.await()){
            is Result.Success -> DataRepositoryResponse.Data(result.value)
            is Result.Failure -> DataRepositoryResponse.Error(result.error)
        }
    }

}

sealed class DataRepositoryResponse<out D, out E>{
    data class Data<T>(val value : T) : DataRepositoryResponse<T,Nothing>()
    data class Error(val error : Throwable) : DataRepositoryResponse<Nothing,Throwable>()
}