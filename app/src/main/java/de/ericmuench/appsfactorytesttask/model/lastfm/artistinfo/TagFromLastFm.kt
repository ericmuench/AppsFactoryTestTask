package de.ericmuench.appsfactorytesttask.model.lastfm.artistinfo


import com.google.gson.annotations.SerializedName

data class TagFromLastFm(
    @SerializedName("name")
    val name: String,
    @SerializedName("url")
    val url: String
)