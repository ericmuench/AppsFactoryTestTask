package de.ericmuench.appsfactorytesttask.model.lastfm.artistinfo


import com.google.gson.annotations.SerializedName

data class ArtistInfoFromLastFm(
    @SerializedName("artist")
    val artist: ArtistFromLastFm
)