package de.ericmuench.appsfactorytesttask.model.lastfm.artistsearch


import com.google.gson.annotations.SerializedName

data class SearchedArtistFromLastFm(
    @SerializedName("image")
    val image: List<ImageFromLastFm>,
    @SerializedName("listeners")
    val listeners: String,
    @SerializedName("mbid")
    val mbid: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("streamable")
    val streamable: String,
    @SerializedName("url")
    val url: String
)