package de.ericmuench.appsfactorytesttask.model.repository

import android.content.Context
import androidx.lifecycle.LiveData
import de.ericmuench.appsfactorytesttask.clerk.mapper.DatabaseModelToRuntimeMapper
import de.ericmuench.appsfactorytesttask.clerk.network.LastFmApiClient
import de.ericmuench.appsfactorytesttask.model.repository.util.DataRepositoryResponse
import de.ericmuench.appsfactorytesttask.model.runtime.Album
import de.ericmuench.appsfactorytesttask.model.runtime.Artist
import de.ericmuench.appsfactorytesttask.model.runtime.ArtistSearchResult
import de.ericmuench.appsfactorytesttask.model.runtime.TopAlbumOfArtistResult
import de.ericmuench.appsfactorytesttask.model.repository.network.ArtistSearchNetworkRepository
import de.ericmuench.appsfactorytesttask.model.repository.network.RuntimeNetworkRepository
import de.ericmuench.appsfactorytesttask.model.room.StoredAlbumInfo
import de.ericmuench.appsfactorytesttask.util.errorhandling.ContextReferenceResourceExceptionGenerator
import de.ericmuench.appsfactorytesttask.util.errorhandling.ResourceThrowableGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.lang.Exception

/**
 * This object defines the global DataSource for the runtime Model. All Data is loaded from here.
 * It does abstraction over Network calls or local DB- data access so that higher architecture-
 * layer do not have to care about the origin of the data. Additionally to that, runtime-caching
 * is provided. For all that, the DataRepository uses internal Sub-Repositories and manages
 * their interaction with each other.
 */
class DataRepository(
    context : Context
) : ResourceThrowableGenerator by ContextReferenceResourceExceptionGenerator(context) {

    //region fields
    /**This field is responsible for all network API-Calls*/
    private val apiClient = LastFmApiClient()
    private val artistSearchRepository = ArtistSearchNetworkRepository(apiClient,context)
    private val runtimeRepository = RuntimeNetworkRepository(apiClient,context)
    private val databaseRepository = DatabaseRepository(context)

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
    ) : DataRepositoryResponse<ArtistSearchResult, Throwable> = coroutineScope{
        return@coroutineScope artistSearchRepository
            .searchForArtists(hasInternet,searchQuery, startPage, limitPerPage)
    }

    suspend fun lastArtistSearchResult() : DataRepositoryResponse<List<ArtistSearchResult>, Throwable>
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
    ) : DataRepositoryResponse<Artist, Throwable> = coroutineScope{
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
    ): DataRepositoryResponse<TopAlbumOfArtistResult, Throwable> = coroutineScope{
        return@coroutineScope runtimeRepository
            .getTopAlbumsByArtistName(hasInternet,artistName, startPage, limitPerPage,shouldRefreshRuntimeCache)
    }
    //endregion

    //region functions for Database
    suspend fun isAlbumStored(album: Album) : DataRepositoryResponse<Boolean, Throwable> = coroutineScope{
        return@coroutineScope databaseRepository.isAlbumStored(album)
    }

    /**
     * This function can store an Album in the local Database. Before it has to look up the Artist
     * for the Album in the Cache of the Runtime-Repository.
     *
     * @param album The Album that should be stored(merged)
     *
     * @return A DataRepository-Response whether the Insert was successful or an Error
     * */
    suspend fun storeAlbum(album: Album) : DataRepositoryResponse<Boolean, Throwable> = coroutineScope {
        val artistDef = async(Dispatchers.IO){
            //loading artist for an album: Usually the artist should be in the cache of the runtime repo
            // if he/she is not in the runtime repo and not in the DB, storing will fail
            runtimeRepository.getArtistByName(true,album.artistName)
        }

        val artistRes = artistDef.await()

        return@coroutineScope when(artistRes){
            is DataRepositoryResponse.Error -> DataRepositoryResponse.Error(artistRes.error)
            is DataRepositoryResponse.Data -> databaseRepository.storeAlbumWithAssociatedData(album,artistRes.value)
        }
    }


    /**
     * This function can remove an Album in the local Database. Before it has to look up the Artist
     * for the Album in the Cache of the Runtime-Repository.
     *
     * @param album The Album that should be stored(merged)
     *
     * @return A DataRepository-Response whether the Insert was successful or an Error
     * */
    suspend fun unstoreAlbum(album: Album) : DataRepositoryResponse<Boolean, Throwable> = coroutineScope {
        return@coroutineScope databaseRepository.unstoreAlbumWithAssociatedData(album)
    }

    /**
     * This function creates an Album-Object based on the Information from the Database and the
     * and the given Id from the AlbumInfo.
     * @param albumInfo An AlbumInfo containing an Id for getting Album-Information from DB
     *
     * @return A DataRepository-Response with a valid album or a throwable if an error occurred.
     * */
    suspend fun getAlbumByStoredAlbumInfo(
        albumInfo : StoredAlbumInfo
    ) : DataRepositoryResponse<Album,Throwable> = coroutineScope{

        val resultDef = async{
            try{
                val albumId = albumInfo.alid
                //starting coroutines
                val storedAlbumResultDef = async{
                    databaseRepository.getAlbumById(albumId)
                }

                val songsResultDef = async{
                    databaseRepository.getSongsByAlbumId(albumId)
                }

                //awaiting coroutines + error handling
                val storedAlbumResult = storedAlbumResultDef.await()
                return@async when(storedAlbumResult){
                    is DataRepositoryResponse.Error -> {
                        songsResultDef.cancel()
                        storedAlbumResult
                    }
                    is DataRepositoryResponse.Data -> {

                        when(val songsResult = songsResultDef.await()){
                            is DataRepositoryResponse.Error ->  songsResult
                            is DataRepositoryResponse.Data -> {
                                //creating album
                                val dataMapper = DatabaseModelToRuntimeMapper()
                                val mapped = dataMapper.mapAlbum(
                                    storedAlbumResult.value,
                                    albumInfo.artistName,
                                    songsResult.value)
                                DataRepositoryResponse.Data(mapped)
                            }
                        }
                    }
                }
            }
            catch(ex: Exception){
                return@async DataRepositoryResponse.Error(ex)
            }
        }
        return@coroutineScope resultDef.await()
    }

    /**
     * This function returns LiveData from the Database containing AlbumInfo-Objects about all
     * stored Albums
     * */
    fun allStoredAlbumsInfoLiveData() : LiveData<List<StoredAlbumInfo>> = databaseRepository
        .allStoredAlbumsLiveData()
    //endregion

}
