package de.ericmuench.appsfactorytesttask.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.ericmuench.appsfactorytesttask.model.repository.DataRepository
import de.ericmuench.appsfactorytesttask.model.room.StoredAlbum
import de.ericmuench.appsfactorytesttask.model.room.StoredAlbumInfo

class StoredAlbumsViewModel(repository: DataRepository) : ViewModel() {

    //region LiveData
    val allStoredAlbums : LiveData<List<StoredAlbumInfo>> = repository.allStoredAlbumsLiveData()
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