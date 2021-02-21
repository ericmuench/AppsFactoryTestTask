package de.ericmuench.appsfactorytesttask.model.lastfm.albuminfo


import com.google.gson.annotations.SerializedName

data class WikiFromLastFm(
    @SerializedName("content")
    val content: String,
    @SerializedName("published")
    val published: String,
    @SerializedName("summary")
    val summary: String
)