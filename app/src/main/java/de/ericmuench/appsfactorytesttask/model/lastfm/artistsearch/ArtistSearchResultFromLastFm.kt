package de.ericmuench.appsfactorytesttask.model.lastfm.artistsearch


import com.google.gson.annotations.SerializedName

data class ArtistSearchResultFromLastFm(
    @SerializedName("results")
    val artistResults: ArtistResultsFromLastFm
)