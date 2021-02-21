package de.ericmuench.appsfactorytesttask.viewmodel

import de.ericmuench.appsfactorytesttask.model.runtime.Artist

class ArtistDetailViewModel : DetailViewModel<Artist>() {
    //region Implemented Abstract Functions From Upper Classes and Interfaces
    override fun loadData(onError: (Throwable) -> Unit) {
        TODO("Not yet implemented")
    }
    //endregion
}