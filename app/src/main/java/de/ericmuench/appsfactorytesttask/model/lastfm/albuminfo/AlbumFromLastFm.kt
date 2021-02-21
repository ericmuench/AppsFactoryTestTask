package de.ericmuench.appsfactorytesttask.model.lastfm.albuminfo


import com.google.gson.annotations.SerializedName
import de.ericmuench.appsfactorytesttask.model.lastfm.image.ImageFromLastFm

data class AlbumFromLastFm(
    @SerializedName("mbid")
    val mbid: String?,
    @SerializedName("artist")
    val artist: String,
    @SerializedName("image")
    val image: List<ImageFromLastFm>,
    @SerializedName("listeners")
    val listeners: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("playcount")
    val playcount: String,
    @SerializedName("tags")
    val tags: TagsFromLastFm,
    @SerializedName("tracks")
    val tracks: TracksFromLastFm,
    @SerializedName("url")
    val url: String,
    @SerializedName("wiki")
    val wiki: WikiFromLastFm
)