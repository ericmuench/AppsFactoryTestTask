package de.ericmuench.appsfactorytesttask.model.runtime.repository

import de.ericmuench.appsfactorytesttask.clerk.network.LastFmApiClient
import de.ericmuench.appsfactorytesttask.model.runtime.Artist
import de.ericmuench.appsfactorytesttask.model.runtime.ArtistSearchResult
import de.ericmuench.appsfactorytesttask.util.connectivity.ConnectivityChecker
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
        connectivityChecker: ConnectivityChecker,
        searchQuery : String,
        startPage : Int = 1,
        limitPerPage : Int = 10
    ) : DataRepositoryResponse<ArtistSearchResult,Throwable> = coroutineScope{
        return@coroutineScope artistSearchRepository
            .searchForArtists(connectivityChecker, searchQuery, startPage, limitPerPage)
    }

    suspend fun lastArtistSearchResult() : DataRepositoryResponse<List<ArtistSearchResult>,Throwable>
        = coroutineScope{
        return@coroutineScope artistSearchRepository.lastArtistSearchResult()
    }
    //endregion

    //region functions for runtime access to artists
    /**See Documentation for this function in RuntimeRepository*/
    suspend fun getArtistByMbid(
        connectivityChecker: ConnectivityChecker,
        mbid: String,
        shouldIgnoreRuntimeCache : Boolean = false
    ) : DataRepositoryResponse<Artist,Throwable> = coroutineScope{
        return@coroutineScope runtimeRepository
            .getArtistByMbid(connectivityChecker,mbid,shouldIgnoreRuntimeCache)
    }
    //endregion


}
