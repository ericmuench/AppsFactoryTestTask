package de.ericmuench.appsfactorytesttask.model.lastfm.artistsearch


import com.google.gson.annotations.SerializedName

data class ArtistmatchesFromLastFm(
    @SerializedName("artist")
    val searchedArtist: List<SearchedArtistFromLastFm>
)