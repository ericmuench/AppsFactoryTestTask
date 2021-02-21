package de.ericmuench.appsfactorytesttask.model.lastfm.artistinfo


import com.google.gson.annotations.SerializedName

data class SimilarFromLastFm(
    @SerializedName("artist")
    val artist: List<ArtistFromLastFmX>
)