package de.ericmuench.appsfactorytesttask.viewmodel

import androidx.lifecycle.*
import de.ericmuench.appsfactorytesttask.model.runtime.Artist
import de.ericmuench.appsfactorytesttask.model.repository.DataRepository
import de.ericmuench.appsfactorytesttask.model.runtime.ArtistSearchResult
import de.ericmuench.appsfactorytesttask.model.repository.util.DataRepositoryResponse
import de.ericmuench.appsfactorytesttask.util.extensions.notNullSuspending
import de.ericmuench.appsfactorytesttask.util.loading.LoadingState
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * This class defines a ViewModel for the View that is responsible for searching artists
 */
class SearchArtistViewModel(private val dataRepository: DataRepository) : ViewModel() {

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
        get() = dataRepository.isSearchingArtist
        set(value) {
            dataRepository.isSearchingArtist = value
        }

    var artistSearchQuery : String
        get() = dataRepository.artistSearchQuery
        set(value) {
            dataRepository.artistSearchQuery = value
        }

    var pendingArtistSearchQuery : String
        get() = dataRepository.pendingArtistSearchQuery
        set(value) {
            dataRepository.pendingArtistSearchQuery = value
        }

    val allArtists : List<Artist>
    get() = searchedArtistsResultChunks.value
            ?.map { it.items }
            ?.flatten()
            ?: emptyList()

    //functions
    fun submitArtistSearchQuery(
        hasInternet : Boolean,
        onError : (Throwable) -> Unit = {}
    ) = viewModelScope.launch{
        _loadingState.value = LoadingState.LOADING
        clearArtistSearchData()
        val job = launch { loadData(hasInternet,onError,1) }
        job.join()
        _loadingState.value = LoadingState.IDLE
    }

    fun loadMoreSearchData(
        hasInternet : Boolean,
        onError : (Throwable) -> Unit = {}
    ) = viewModelScope.launch{
        _searchedArtistsResultChunks.value.notNullSuspending { currentResults ->
            if(currentResults.isNotEmpty()){
                _loadingState.value = LoadingState.LOADING_MORE
                val page = currentResults.last().startPage + 1
                val job = launch { loadData(hasInternet,onError,page) }
                job.join()
                _loadingState.value = LoadingState.IDLE
            }
        }
    }

    /**This function loads the latest cached data for the Search from the DataRepository*/
    fun loadLastSearchResults() = viewModelScope.launch {
        val lastSearchRes = dataRepository.lastArtistSearchResult()
        if (lastSearchRes is DataRepositoryResponse.Data<List<ArtistSearchResult>>) {
            _searchedArtistsResultChunks.value = lastSearchRes.value
        }
    }


    fun clearArtistSearchData(){
        _searchedArtistsResultChunks.value = emptyList()
    }

    fun hasSearchResults() : Boolean = allArtists.isNotEmpty()

    //help functions
    /**
     * This function is responsible for the data-load-Operation from the DataRepository. After all
     * data was loaded it should be applied to the LiveData-Fields or OnError should be invoked
     * @param onError Callback to be executed when error occurs
     * @param startPage The Page to start the search
     *
     */
    private suspend fun loadData(
        hasInternet : Boolean,
        onError : (Throwable) -> Unit = {},
        startPage : Int
    ) = coroutineScope{
        val repoResponse = dataRepository.searchForArtists(hasInternet,artistSearchQuery,startPage)
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


class SearchArtistViewModelFactory(private val repository: DataRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchArtistViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchArtistViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
