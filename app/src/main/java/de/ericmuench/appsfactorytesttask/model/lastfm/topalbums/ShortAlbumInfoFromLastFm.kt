package de.ericmuench.appsfactorytesttask.model.lastfm.topalbums


import com.google.gson.annotations.SerializedName
import de.ericmuench.appsfactorytesttask.model.lastfm.image.ImageFromLastFm

data class ShortAlbumInfoFromLastFm(
    @SerializedName("artist")
    val artist: ArtistFromLastFm,
    @SerializedName("image")
    val image: List<ImageFromLastFm>,
    @SerializedName("mbid")
    val mbid: String?,
    @SerializedName("name")
    val name: String,
    @SerializedName("playcount")
    val playcount: Int,
    @SerializedName("url")
    val url: String
)