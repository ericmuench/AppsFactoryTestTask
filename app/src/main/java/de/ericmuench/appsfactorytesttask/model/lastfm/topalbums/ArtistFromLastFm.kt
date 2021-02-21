package de.ericmuench.appsfactorytesttask.model.lastfm.topalbums


import com.google.gson.annotations.SerializedName

data class ArtistFromLastFm(
    @SerializedName("mbid")
    val mbid: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("url")
    val url: String
)