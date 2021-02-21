package de.ericmuench.appsfactorytesttask.model.lastfm.artistinfo


import com.google.gson.annotations.SerializedName
import de.ericmuench.appsfactorytesttask.model.lastfm.image.ImageFromLastFm

data class ArtistFromLastFmX(
    @SerializedName("image")
    val image: List<ImageFromLastFm>,
    @SerializedName("name")
    val name: String,
    @SerializedName("url")
    val url: String
)