package de.ericmuench.appsfactorytesttask.model.lastfm.albuminfo


import com.google.gson.annotations.SerializedName

data class AlbumInfoFromLastFm(
    @SerializedName("album")
    val album: AlbumFromLastFm
)