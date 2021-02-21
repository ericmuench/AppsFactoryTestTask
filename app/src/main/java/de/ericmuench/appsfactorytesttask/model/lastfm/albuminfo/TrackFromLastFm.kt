package de.ericmuench.appsfactorytesttask.model.lastfm.albuminfo


import com.google.gson.annotations.SerializedName

data class TrackFromLastFm(
    @SerializedName("artist")
    val artist: ArtistFromLastFm,
    @SerializedName("@attr")
    val attr: AttrFromLastFm,
    @SerializedName("duration")
    val duration: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("streamable")
    val streamable: StreamableFromLastFm,
    @SerializedName("url")
    val url: String
)