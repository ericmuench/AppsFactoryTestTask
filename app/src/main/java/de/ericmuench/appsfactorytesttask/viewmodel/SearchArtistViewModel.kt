package de.ericmuench.appsfactorytesttask.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ericmuench.appsfactorytesttask.model.runtime.Artist
import de.ericmuench.appsfactorytesttask.model.runtime.DataRepository
import de.ericmuench.appsfactorytesttask.model.runtime.DataRepositoryResponse
import de.ericmuench.appsfactorytesttask.model.runtime.ArtistSearchResults
import de.ericmuench.appsfactorytesttask.util.extensions.notNullSuspending
import de.ericmuench.appsfactorytesttask.util.loading.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * This class defines a ViewModel for the View that is responsible for searching artists
 */
class SearchArtistViewModel : ViewModel() {

    init {
        println("Init of SearchArtistVM")
    }
    //LiveData
    /**The following fields represent the list of Artists as a Search-Result*/
    private val _searchedArtistsResultChunks = MutableLiveData<List<ArtistSearchResults>>(emptyList())
    val searchedArtistsResultChunks : LiveData<List<ArtistSearchResults>>
    get() = _searchedArtistsResultChunks

    /**The following field take care of the loading state*/
    private val _loadingState = MutableLiveData(LoadingState.IDLE)
    val loadingState : LiveData<LoadingState>
    get() = _loadingState

    /**The following field holds the current search-query*/
    val searchQuery = MutableLiveData("")

    /**The following field indicates whether the search-mode is currently activated or not*/
    val isSearching = MutableLiveData(false)


    //fields
    //var searchQuery : String = ""
    //var isSearching : Boolean = false
    val allArtists : List<Artist>
    get() = searchedArtistsResultChunks.value
            ?.map { it.items }
            ?.flatten()
            ?: emptyList()

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
    private suspend fun loadData(onError : (Throwable) -> Unit = {}, startPage : Int) = coroutineScope{
        val repoResponse = DataRepository.searchForArtists(searchQuery.value ?: "",startPage)
        when(repoResponse){
            is DataRepositoryResponse.Data<ArtistSearchResults> ->{
                if(repoResponse.value.items.isNotEmpty()){
                    val newChunksDef = async(Dispatchers.IO) {
                        val distinctArtists = repoResponse.value.items
                                .filter { artist ->
                                    !allArtists.contains(artist)
                                }
                        val distinctSearchResults = ArtistSearchResults(
                                repoResponse.value.totalResults,
                                repoResponse.value.startPage,
                                repoResponse.value.startIndex,
                                distinctArtists.size,
                                distinctArtists
                        )
                        _searchedArtistsResultChunks.value
                                ?.toMutableList()
                                ?.apply {
                                    add(distinctSearchResults)
                                }
                    }
                    _searchedArtistsResultChunks.value = newChunksDef.await()
                }
            }
            is DataRepositoryResponse.Error -> onError(repoResponse.error)
        }
    }

}