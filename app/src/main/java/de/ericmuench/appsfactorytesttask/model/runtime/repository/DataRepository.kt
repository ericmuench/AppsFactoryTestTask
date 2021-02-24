package de.ericmuench.appsfactorytesttask.model.runtime.repository

import de.ericmuench.appsfactorytesttask.clerk.network.LastFmApiClient
import de.ericmuench.appsfactorytesttask.model.runtime.Artist
import de.ericmuench.appsfactorytesttask.model.runtime.ArtistSearchResult
import de.ericmuench.appsfactorytesttask.model.runtime.TopAlbumOfArtistResult
import de.ericmuench.appsfactorytesttask.util.connectivity.InternetConnectivityChecker
import kotlinx.coroutines.coroutineScope

/**
 * This object defines the global DataSource for the runtime Model. All Data is loaded from here.
 * It does abstraction over Network calls or local DB- data access so that higher architecture-
 * layer do not have to care about the origin of the data. Additionally to that, runtime-caching
 * is provided. For all that, the DataRepository uses internal Sub-Repositories and manages
 * their interaction with each other.
 */
object DataRepository {

    //region fields
    /**This field is responsible for all network API-Calls*/
    private val apiClient = LastFmApiClient()
    private val artistSearchRepository = ArtistSearchRepository(apiClient)
    private val runtimeRepository = RuntimeRepository(apiClient)


    //TODO: Add further fields for room and runtime cache

    //region Fields for Search of Artists
    /**This field caches the current Search-Query results and search states*/
    var isSearchingArtist : Boolean
        get() = artistSearchRepository.isSearchingArtist
        set(value) {
            artistSearchRepository.isSearchingArtist = value
        }

    var artistSearchQuery : String
        get() = artistSearchRepository.artistSearchQuery
        set(value) {
            artistSearchRepository.artistSearchQuery = value
        }

    var pendingArtistSearchQuery : String
        get() = artistSearchRepository.pendingArtistSearchQuery
        set(value) {
            artistSearchRepository.pendingArtistSearchQuery = value
        }
    //endregion

    //endregion

    //region functions for artist search
    suspend fun searchForArtists(
        hasInternet : Boolean,
        searchQuery : String,
        startPage : Int = 1,
        limitPerPage : Int = 10
    ) : DataRepositoryResponse<ArtistSearchResult,Throwable> = coroutineScope{
        return@coroutineScope artistSearchRepository
            .searchForArtists(hasInternet,searchQuery, startPage, limitPerPage)
    }

    suspend fun lastArtistSearchResult() : DataRepositoryResponse<List<ArtistSearchResult>,Throwable>
        = coroutineScope{
        return@coroutineScope artistSearchRepository.lastArtistSearchResult()
    }
    //endregion

    //region functions for runtime access to artists
    /**See Documentation for this function in RuntimeRepository*/
    suspend fun getArtistByName(
        hasInternet : Boolean,
        mbid: String,
        shouldIgnoreRuntimeCache : Boolean = false
    ) : DataRepositoryResponse<Artist,Throwable> = coroutineScope{
        return@coroutineScope runtimeRepository
            .getArtistByName(hasInternet,mbid,shouldIgnoreRuntimeCache)
    }

    /**See Documentation for this function in RuntimeRepository*/
    suspend fun getTopAlbumsByArtistName(
        hasInternet : Boolean,
        artistName: String,
        startPage : Int,
        limitPerPage : Int,
        shouldRefreshRuntimeCache : Boolean = false,
    ): DataRepositoryResponse<TopAlbumOfArtistResult,Throwable> = coroutineScope{
        return@coroutineScope runtimeRepository
            .getTopAlbumsByArtistName(hasInternet,artistName, startPage, limitPerPage,shouldRefreshRuntimeCache)
    }
    //endregion


}
