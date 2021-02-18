package de.ericmuench.appsfactorytesttask.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ericmuench.appsfactorytesttask.model.runtime.Artist
import de.ericmuench.appsfactorytesttask.model.runtime.DataRepository
import de.ericmuench.appsfactorytesttask.model.runtime.DataRepositoryResponse
import de.ericmuench.appsfactorytesttask.model.runtime.ArtistSearchResult
import de.ericmuench.appsfactorytesttask.util.connectivity.ConnectivityChecker
import de.ericmuench.appsfactorytesttask.util.extensions.notNullSuspending
import de.ericmuench.appsfactorytesttask.util.loading.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * This class defines a ViewModel for the View that is responsible for searching artists
 */
class SearchArtistViewModel : ViewModel() {

    //LiveData
    /**The following fields represent the list of Artists as a Search-Result*/
    private val _searchedArtistsResultChunks = MutableLiveData<List<ArtistSearchResult>>(emptyList())
    val searchedArtistsResultChunks : LiveData<List<ArtistSearchResult>>
    get() = _searchedArtistsResultChunks

    /**The following field take care of the loading state*/
    private val _loadingState = MutableLiveData(LoadingState.IDLE)
    val loadingState : LiveData<LoadingState>
    get() = _loadingState

    //fields
    var isSearchingArtist : Boolean
        get() = DataRepository.isSearchingArtist
        set(value) {
            DataRepository.isSearchingArtist = value
        }

    var artistSearchQuery : String
        get() = DataRepository.artistSearchQuery
        set(value) {
            DataRepository.artistSearchQuery = value
        }

    var pendingArtistSearchQuery : String
        get() = DataRepository.pendingArtistSearchQuery
        set(value) {
            DataRepository.pendingArtistSearchQuery = value
        }

    val allArtists : List<Artist>
    get() = searchedArtistsResultChunks.value
            ?.map { it.items }
            ?.flatten()
            ?: emptyList()

    //functions
    fun submitArtistSearchQuery(
        connectivityChecker: ConnectivityChecker,
        onError : (Throwable) -> Unit = {}
    ) = viewModelScope.launch{
        _loadingState.value = LoadingState.LOADING
        clearViewModelData()
        val job = launch { loadData(connectivityChecker,onError,1) }
        job.join()
        _loadingState.value = LoadingState.IDLE
    }

    fun loadMoreSearchData(
            connectivityChecker: ConnectivityChecker,
            onError : (Throwable) -> Unit = {}
    ) = viewModelScope.launch{
        _searchedArtistsResultChunks.value.notNullSuspending { currentResults ->
            if(currentResults.isNotEmpty()){
                _loadingState.value = LoadingState.RELOADING
                val page = currentResults.last().startPage + 1
                val job = launch { loadData(connectivityChecker,onError,page) }
                job.join()
                _loadingState.value = LoadingState.IDLE
            }
        }
    }

    /**This function loads the latest cached data for the Search from the DataRepository*/
    fun loadLastSearchResults() = viewModelScope.launch {
        val lastSearchRes = DataRepository.lastArtistSearchResult()
        if (lastSearchRes is DataRepositoryResponse.Data<List<ArtistSearchResult>>) {
            _searchedArtistsResultChunks.value = lastSearchRes.value
        }
    }


    //help functions
    private fun clearViewModelData(){
        _searchedArtistsResultChunks.value = emptyList()
    }

    /**
     * This function is responsible for the data-load-Operation from the DataRepository. After all
     * data was loaded it should be applied to the LiveData-Fields or OnError should be invoked
     * @param onError Callback to be executed when error occurs
     * @param startPage The Page to start the search
     *
     */
    private suspend fun loadData(
            connectivityChecker: ConnectivityChecker,
            onError : (Throwable) -> Unit = {},
            startPage : Int
    ) = coroutineScope{
        val repoResponse = DataRepository.searchForArtists(connectivityChecker,artistSearchQuery,startPage)
        when(repoResponse){
            is DataRepositoryResponse.Data<ArtistSearchResult> ->{
                if(repoResponse.value.items.isNotEmpty()){
                    _searchedArtistsResultChunks.value = _searchedArtistsResultChunks.value
                            ?.toMutableList()
                            ?.apply {
                                add(repoResponse.value)
                            }
                }
            }
            is DataRepositoryResponse.Error -> onError(repoResponse.error)
        }
    }
}