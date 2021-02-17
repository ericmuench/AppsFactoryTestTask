package de.ericmuench.appsfactorytesttask.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kittinunf.result.Result
import de.ericmuench.appsfactorytesttask.clerk.network.LastFmApiClient
import de.ericmuench.appsfactorytesttask.model.runtime.Artist
import de.ericmuench.appsfactorytesttask.model.runtime.DataRepository
import de.ericmuench.appsfactorytesttask.model.runtime.DataRepositoryResponse
import de.ericmuench.appsfactorytesttask.model.runtime.LastFmArtistSearchResults
import de.ericmuench.appsfactorytesttask.util.extensions.notNull
import de.ericmuench.appsfactorytesttask.util.extensions.notNullSuspending
import de.ericmuench.appsfactorytesttask.util.loading.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * This class defines a ViewModel for the View that is responsible for searching artists
 */
class SearchArtistViewModel : ViewModel() {

    //LiveData
    /**The following fields represent the list of Artists as a Search-Result*/
    private val _searchedArtistsResultChunks = MutableLiveData<List<LastFmArtistSearchResults>>(emptyList())
    val searchedArtistsResultChunks : LiveData<List<LastFmArtistSearchResults>>
    get() = _searchedArtistsResultChunks

    /**The following field take care of the loading state*/
    private val _loadingState = MutableLiveData(LoadingState.IDLE)
    val loadingState : LiveData<LoadingState>
    get() = _loadingState

    //fields
    var searchQuery : String = ""

    //functions
    fun submitArtistSearchQuery(
        onError : (Throwable) -> Unit = {}
    ) = viewModelScope.launch{
        _loadingState.value = LoadingState.LOADING
        clearViewModelData()
        val job = launch { loadData(onError,1) }
        job.join()
        _loadingState.value = LoadingState.IDLE
    }

    fun loadMoreSearchData(onError : (Throwable) -> Unit = {}) = viewModelScope.launch{
        _searchedArtistsResultChunks.value.notNullSuspending { currentResults ->
            if(currentResults.isNotEmpty()){
                _loadingState.value = LoadingState.RELOADING
                val page = currentResults.last().startPage + 1
                val job = launch { loadData(onError,page) }
                job.join()
                _loadingState.value = LoadingState.IDLE
            }
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
    private suspend fun loadData(onError : (Throwable) -> Unit = {}, startPage : Int){
        val repoResponse = DataRepository.searchForArtists(searchQuery,startPage)
        when(repoResponse){
            is DataRepositoryResponse.Data<LastFmArtistSearchResults> ->{
                if(repoResponse.value.items.isNotEmpty()){
                    _searchedArtistsResultChunks.value =
                        _searchedArtistsResultChunks
                            .value
                            ?.toMutableList()
                            ?.apply { add(repoResponse.value) }
                }
            }
            is DataRepositoryResponse.Error -> onError(repoResponse.error)
        }
    }

}