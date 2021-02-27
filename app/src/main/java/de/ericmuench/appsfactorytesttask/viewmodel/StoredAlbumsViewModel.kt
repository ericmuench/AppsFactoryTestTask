package de.ericmuench.appsfactorytesttask.viewmodel

import androidx.lifecycle.*
import de.ericmuench.appsfactorytesttask.model.repository.DataRepository
import de.ericmuench.appsfactorytesttask.model.repository.DatabaseRepository
import de.ericmuench.appsfactorytesttask.model.repository.util.DataRepositoryResponse
import de.ericmuench.appsfactorytesttask.model.room.StoredAlbum
import de.ericmuench.appsfactorytesttask.model.room.StoredAlbumInfo
import de.ericmuench.appsfactorytesttask.model.runtime.Album
import de.ericmuench.appsfactorytesttask.util.errorhandling.OnErrorHandler
import de.ericmuench.appsfactorytesttask.util.extensions.getDistinct
import kotlinx.coroutines.launch

class StoredAlbumsViewModel(private val repository: DataRepository) : ViewModel() {

    //region LiveData
    val allStoredAlbums : LiveData<List<StoredAlbumInfo>> = repository
        .allStoredAlbumsInfoLiveData()
        .getDistinct()
    //endregion

    //region Functions
    /**
     * This function loads an Album-Object for the given StoredAlbumInfo and then executes a Callback
     * with it.
     *
     * @param albumInfo The AlbumInfo that should be used to load the actual Album from the DB
     * (which is then mapped to an Album used at Runtime)
     * @param withAlbumFunc A Callback to execute when the associated album was loaded
     * @param onError A Error-Callback to be executed when the loading failed
     * */
    fun withFullAlbumForInfo(
        albumInfo : StoredAlbumInfo,
        onError: (Throwable) -> Unit = {},
        withAlbumFunc : (Album) -> Unit
    ) = viewModelScope.launch {
        val albumResult = repository.getAlbumByStoredAlbumInfo(albumInfo)

        when(albumResult){
            is DataRepositoryResponse.Error -> onError(albumResult.error)
            is DataRepositoryResponse.Data -> withAlbumFunc(albumResult.value)
        }

    }
    //endregion
}

class StoredAlbumsViewModelFactory(private val repository: DataRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoredAlbumsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StoredAlbumsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}