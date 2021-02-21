package de.ericmuench.appsfactorytesttask.model.lastfm.albuminfo


import com.google.gson.annotations.SerializedName

data class StreamableFromLastFm(
    @SerializedName("fulltrack")
    val fulltrack: String,
    @SerializedName("#text")
    val text: String
)