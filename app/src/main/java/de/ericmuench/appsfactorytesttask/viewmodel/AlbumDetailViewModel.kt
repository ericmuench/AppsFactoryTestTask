package de.ericmuench.appsfactorytesttask.viewmodel

import androidx.lifecycle.*
import de.ericmuench.appsfactorytesttask.model.runtime.Album
import de.ericmuench.appsfactorytesttask.model.runtime.repository.DataRepository
import de.ericmuench.appsfactorytesttask.model.runtime.repository.DataRepositoryResponse
import de.ericmuench.appsfactorytesttask.util.extensions.notNull
import de.ericmuench.appsfactorytesttask.util.loading.LoadingState
import de.ericmuench.appsfactorytesttask.viewmodel.abstract_viewmodels.DetailViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class AlbumDetailViewModel(private val dataRepository: DataRepository) : DetailViewModel<Album>() {

    //region LiveData
    /**This field indicates whether the currently displayed Album is stored in the Database or not*/
    private val _isAlbumStored = MutableLiveData<Boolean>()
    val isAlbumStored : LiveData<Boolean>
    get() = _isAlbumStored

    private val _isProcessing = MutableLiveData<LoadingState>(LoadingState.IDLE)
    val isProcessing : LiveData<LoadingState>
    get() = _isProcessing
    //endregion

    //region Functions
    fun checkAlbumExistence(album: Album, onError : (Throwable) -> Unit = {}) = viewModelScope.launch {
        when(val albumExistsResponse = dataRepository.isAlbumStored(album)){
            is DataRepositoryResponse.Data -> {
                _isAlbumStored.value = albumExistsResponse.value
            }
            is DataRepositoryResponse.Error -> onError(albumExistsResponse.error)
        }
    }

    fun switchStoreState(onError : (Throwable) -> Unit = {}) = viewModelScope.launch{
        val isStored = isAlbumStored.value ?: return@launch
        val album = detailData.value ?: return@launch

        _isProcessing.value = LoadingState.LOADING
        if(isStored){
            //unstoreAlbum(album,onError)
        }
        else{
            storeAlbum(album,onError)
        }

        _isProcessing.value = LoadingState.IDLE
    }

    private suspend fun storeAlbum(album: Album, onError : (Throwable) -> Unit = {}) = coroutineScope{
        when(val storeResponse = dataRepository.storeAlbum(album)){
            is DataRepositoryResponse.Data -> {
                detailData.value.notNull {
                    println("storing was successful : ${storeResponse.value}")
                    checkAlbumExistence(album, onError)
                }
            }
            is DataRepositoryResponse.Error -> onError(storeResponse.error)
        }
    }

    private suspend fun unstoreAlbum(album: Album, onError : (Throwable) -> Unit = {}) = coroutineScope{
        when(val storeResponse = dataRepository.unstoreAlbum(album)){
            is DataRepositoryResponse.Data -> {
                detailData.value.notNull {
                    println("unstoring was successful : ${storeResponse.value}")
                    checkAlbumExistence(album, onError)
                }
            }
            is DataRepositoryResponse.Error -> onError(storeResponse.error)
        }
    }
    //endregion


}

class AlbumsDetailViewModelFactory(private val repository: DataRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlbumDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlbumDetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

