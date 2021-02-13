package de.ericmuench.appsfactorytesttask.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kittinunf.result.Result
import de.ericmuench.appsfactorytesttask.clerk.network.LastFmApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * This class defines a ViewModel for the View that is responsible for searching artists
 */
class SearchArtistViewModel : ViewModel() {
    //fields
    private val apiClient = LastFmApiClient()

    //functions
    fun submitArtistSearchQuery(query: String) = viewModelScope.launch{
        val resultDeferred = async(Dispatchers.IO) {
            apiClient.searchArtists(query,1)
        }

        when(val result = resultDeferred.await()){
            is Result.Success -> result.value.items.forEach {
                println(it)
            }
            is Result.Failure -> result.error.printStackTrace()
        }
    }

}