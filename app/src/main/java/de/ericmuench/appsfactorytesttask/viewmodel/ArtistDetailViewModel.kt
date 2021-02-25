package de.ericmuench.appsfactorytesttask.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import de.ericmuench.appsfactorytesttask.model.runtime.Album
import de.ericmuench.appsfactorytesttask.model.runtime.Artist
import de.ericmuench.appsfactorytesttask.model.runtime.TopAlbumOfArtistResult
import de.ericmuench.appsfactorytesttask.model.runtime.repository.DataRepository
import de.ericmuench.appsfactorytesttask.model.runtime.repository.DataRepositoryResponse
import de.ericmuench.appsfactorytesttask.util.connectivity.InternetConnectivityChecker
import de.ericmuench.appsfactorytesttask.util.errorhandling.OnErrorHandler
import de.ericmuench.appsfactorytesttask.util.extensions.notNullSuspending
import de.ericmuench.appsfactorytesttask.util.loading.LoadingState
import de.ericmuench.appsfactorytesttask.viewmodel.abstract_viewmodels.DetailViewModel
import kotlinx.coroutines.*

class ArtistDetailViewModel : DetailViewModel<Artist>() {

    //region LiveData
    private val _topAlbumResults = MutableLiveData<List<TopAlbumOfArtistResult>>(emptyList())
    val topAlbumResults : LiveData<List<TopAlbumOfArtistResult>>
    get() = _topAlbumResults

    private val _detailLoadingState = MutableLiveData<LoadingState>(LoadingState.IDLE)
    val detailLoadingState : LiveData<LoadingState>
    get() = _detailLoadingState

    private val _albumsLoadingState = MutableLiveData<LoadingState>(LoadingState.IDLE)
    val albumsLoadingState : LiveData<LoadingState>
    get() = _albumsLoadingState
    //endregion

    //region Fields
    val allTopAlbums : List<Album>
    get() = topAlbumResults
        .value
        ?.map { it.albums }
        ?.flatten() ?: emptyList()
    //endregion

    //region Functions
    fun loadDataInitially(hasInternet: Boolean,onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            detailData.value.notNullSuspending {artist ->

                val onErrorHandler = OnErrorHandler(onError)

                val artistLoadingState = detailLoadingState.value ?: LoadingState.IDLE
                if(!artistLoadingState.isLoading){
                    launch {
                        loadDetails(hasInternet, artist,LoadingState.LOADING,false,onErrorHandler)
                    }
                }

                launch {
                    _topAlbumResults.value = emptyList()
                    loadAlbumData(
                        hasInternet = hasInternet,
                        onError = onErrorHandler,
                        artistName = artist.artistName
                    )
                }
            }

        }
    }

    fun reloadData(hasInternet: Boolean,onError: (Throwable) -> Unit = {}) {
        viewModelScope.launch {
            detailData.value.notNullSuspending {artist ->

                val onErrorHandler = OnErrorHandler(onError)

                val artistLoadingState = detailLoadingState.value ?: LoadingState.IDLE
                if(!artistLoadingState.isLoading){
                    launch {
                        loadDetails(hasInternet, artist,LoadingState.RELOADING,true,onErrorHandler)
                    }
                }

                launch {
                    _topAlbumResults.value = emptyList()
                    loadAlbumData(
                        hasInternet = hasInternet,
                        onError = onErrorHandler,
                        artistName = artist.artistName,
                        loadMode = LoadingState.RELOADING,
                        shouldRefreshRuntimeCache = true
                    )
                }
            }
        }
    }


    fun loadMoreAlbumData(
        hasInternet: Boolean,
        onError: (Throwable) -> Unit = {}
    ) = viewModelScope.launch{
        detailData.value.notNullSuspending {artist ->
            val existingResults = topAlbumResults.value
            if(existingResults != null && existingResults.isNotEmpty()){
                val lastPage = existingResults.last().page

                if(lastPage <= existingResults.last().totalPages){
                    loadAlbumData(
                        hasInternet = hasInternet,
                        onError = OnErrorHandler(onError),
                        artistName = artist.artistName,
                        startPage = lastPage + 1,
                        loadMode = LoadingState.LOADING_MORE
                    )
                }
            }
        }
    }
    //endregion

    //region Data Loading Functions
    private suspend fun loadAlbumData(
        hasInternet: Boolean,
        artistName: String,
        startPage: Int = 1,
        limitPerPage: Int = 10,
        loadMode : LoadingState = LoadingState.LOADING,
        shouldRefreshRuntimeCache : Boolean = false,
        onError: OnErrorHandler = OnErrorHandler {  }
    ) = coroutineScope{
        val albumsLoading = albumsLoadingState.value ?: LoadingState.IDLE
        if(albumsLoading.isLoading){
            return@coroutineScope
        }

        _albumsLoadingState.value = loadMode

        val topAlbumsResponse = DataRepository.getTopAlbumsByArtistName(
             hasInternet,artistName, startPage, limitPerPage, shouldRefreshRuntimeCache
        )
        when(topAlbumsResponse){
            is DataRepositoryResponse.Data -> {
                val newDataDef =async(Dispatchers.IO){
                    val newData = topAlbumResults.value?.toMutableList() ?: mutableListOf()
                    if(topAlbumsResponse.value.albums.isNotEmpty()){
                        newData.add(topAlbumsResponse.value)
                    }
                    return@async newData
                }

                _topAlbumResults.value = newDataDef.await()
            }
            is DataRepositoryResponse.Error -> {
                if(!onError.wasAlreadyExecuted){
                    onError(topAlbumsResponse.error)
                }
            }
        }
        _albumsLoadingState.value = LoadingState.IDLE
    }

    private suspend fun loadDetails(
        hasInternet: Boolean,
        artist : Artist,
        loadMode : LoadingState = LoadingState.LOADING,
        shouldIgnoreRuntimeCache : Boolean = false,
        onError: OnErrorHandler = OnErrorHandler {  }
    ){
        _detailLoadingState.value = loadMode
        val artistRepoResponse = DataRepository.getArtistByName(
            hasInternet,
            artist.artistName,
            shouldIgnoreRuntimeCache
        )

        when(artistRepoResponse){
            is DataRepositoryResponse.Data -> {
                setDetailDataValue(artistRepoResponse.value)
            }
            is DataRepositoryResponse.Error -> {
                if(!onError.wasAlreadyExecuted){
                    onError(artistRepoResponse.error)
                }
            }
        }
        _detailLoadingState.value = LoadingState.IDLE
    }
    //endregion
}