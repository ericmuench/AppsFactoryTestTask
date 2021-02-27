package de.ericmuench.appsfactorytesttask.viewmodel

import androidx.lifecycle.*
import de.ericmuench.appsfactorytesttask.model.runtime.Album
import de.ericmuench.appsfactorytesttask.model.runtime.Artist
import de.ericmuench.appsfactorytesttask.model.runtime.TopAlbumOfArtistResult
import de.ericmuench.appsfactorytesttask.model.repository.DataRepository
import de.ericmuench.appsfactorytesttask.model.repository.util.DataRepositoryResponse
import de.ericmuench.appsfactorytesttask.util.errorhandling.OnErrorHandler
import de.ericmuench.appsfactorytesttask.util.extensions.notNullSuspending
import de.ericmuench.appsfactorytesttask.util.loading.LoadingState
import de.ericmuench.appsfactorytesttask.viewmodel.abstract_viewmodels.DetailViewModel
import kotlinx.coroutines.*

class ArtistDetailViewModel(private val dataRepository : DataRepository) : DetailViewModel<Artist>() {

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

        val topAlbumsResponse = dataRepository.getTopAlbumsByArtistName(
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
        val artistRepoResponse = dataRepository.getArtistByName(
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

    //region Functions for Storing Albums in Database
    fun checkAlbumExistence(
            album: Album,
            onError : (Throwable) -> Unit = {},
            onSuccess : (Boolean) -> Unit
    ) = viewModelScope.launch {
        when(val albumExistsResponse = dataRepository.isAlbumStored(album)){
            is DataRepositoryResponse.Data -> onSuccess(albumExistsResponse.value)
            is DataRepositoryResponse.Error -> onError(albumExistsResponse.error)
        }
    }

    fun switchStoreState(
            album: Album,
            onError : (Throwable) -> Unit = {},
            onDone : (Boolean) -> Unit
    ) = viewModelScope.launch{
        when(val albumExistsResponse = dataRepository.isAlbumStored(album)){
            is DataRepositoryResponse.Data -> {
                val isStored = albumExistsResponse.value

                val storeResult = if(isStored){
                    dataRepository.unstoreAlbum(album)
                }
                else{
                    dataRepository.storeAlbum(album)
                }

                when(storeResult){
                    is DataRepositoryResponse.Error -> onError(storeResult.error)
                    is DataRepositoryResponse.Data -> onDone(storeResult.value)
                }
            }
            is DataRepositoryResponse.Error -> onError(albumExistsResponse.error)
        }
    }
    //endregion
}

class ArtistDetailViewModelFactory(private val repository: DataRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArtistDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ArtistDetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}