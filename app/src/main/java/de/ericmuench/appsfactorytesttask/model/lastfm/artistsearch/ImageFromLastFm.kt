package de.ericmuench.appsfactorytesttask.model.lastfm.artistsearch


import com.google.gson.annotations.SerializedName

data class ImageFromLastFm(
    @SerializedName("size")
    val size: String,
    @SerializedName("#text")
    val text: String
)