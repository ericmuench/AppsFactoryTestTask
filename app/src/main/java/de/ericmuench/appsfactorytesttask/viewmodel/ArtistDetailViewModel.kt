package de.ericmuench.appsfactorytesttask.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import de.ericmuench.appsfactorytesttask.model.runtime.Album
import de.ericmuench.appsfactorytesttask.model.runtime.Artist
import de.ericmuench.appsfactorytesttask.model.runtime.repository.DataRepository
import de.ericmuench.appsfactorytesttask.model.runtime.repository.DataRepositoryResponse
import de.ericmuench.appsfactorytesttask.util.connectivity.ConnectivityChecker
import de.ericmuench.appsfactorytesttask.util.extensions.notNullSuspending
import kotlinx.coroutines.launch

class ArtistDetailViewModel : DetailViewModel<Artist>() {

    //region LiveData
    private val _albums = MutableLiveData<List<Album>>(emptyList())
    val albums : LiveData<List<Album>>
    get() = _albums
    //endregion

    //region Implemented Abstract Functions From Upper Classes and Interfaces
    override fun loadData(onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            detailData.value.notNullSuspending {artist ->
                val artistRepoResponse = DataRepository.getArtistByMbid(ConnectivityChecker(),artist.mbid)
                when(artistRepoResponse){
                    is DataRepositoryResponse.Data -> {
                        setDetailDataValue(artistRepoResponse.value)
                    }
                    is DataRepositoryResponse.Error -> onError(artistRepoResponse.error)
                }
            }

        }
        //TODO: load albums
    }
    //endregion
}