package de.ericmuench.appsfactorytesttask.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import de.ericmuench.appsfactorytesttask.model.runtime.Album
import de.ericmuench.appsfactorytesttask.model.runtime.Artist
import de.ericmuench.appsfactorytesttask.model.runtime.TopAlbumOfArtistResult
import de.ericmuench.appsfactorytesttask.model.runtime.repository.DataRepository
import de.ericmuench.appsfactorytesttask.model.runtime.repository.DataRepositoryResponse
import de.ericmuench.appsfactorytesttask.util.connectivity.ConnectivityChecker
import de.ericmuench.appsfactorytesttask.util.extensions.notNullSuspending
import kotlinx.coroutines.launch

class ArtistDetailViewModel : DetailViewModel<Artist>() {

    //region LiveData
    private val _topAlbumResults = MutableLiveData<List<TopAlbumOfArtistResult>>(emptyList())
    val topAlbumResults : LiveData<List<TopAlbumOfArtistResult>>
    get() = _topAlbumResults
    //endregion

    //region Fields
    val allTopAlbums : List<Album>
    get() = topAlbumResults
        .value
        ?.map { it.albums }
        ?.flatten() ?: emptyList()
    //endregion

    //region Implemented Abstract Functions From Upper Classes and Interfaces
    override fun loadData(onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            detailData.value.notNullSuspending {artist ->
                val conCheck = ConnectivityChecker()
                launch {
                    val artistRepoResponse = DataRepository.getArtistByName(conCheck,artist.artistName)
                    when(artistRepoResponse){
                        is DataRepositoryResponse.Data -> {
                            setDetailDataValue(artistRepoResponse.value)
                        }
                        is DataRepositoryResponse.Error -> onError(artistRepoResponse.error)
                    }
                }


                launch {
                    val topAlbumsResponse = DataRepository.getTopAlbumsByArtistName(
                        conCheck,
                        artist.artistName
                    )

                    when(topAlbumsResponse){
                        is DataRepositoryResponse.Data -> {
                            _topAlbumResults.value = topAlbumResults.value
                                ?.toMutableList()
                                ?.apply {
                                    add(topAlbumsResponse.value)
                                }
                        }
                        is DataRepositoryResponse.Error -> onError(topAlbumsResponse.error)
                    }
                }
            }

        }
    }
    //endregion
}