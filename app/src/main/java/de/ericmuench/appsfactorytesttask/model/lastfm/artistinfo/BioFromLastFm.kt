package de.ericmuench.appsfactorytesttask.model.lastfm.artistinfo


import com.google.gson.annotations.SerializedName

data class BioFromLastFm(
    @SerializedName("content")
    val content: String,
    @SerializedName("links")
    val links: LinksFromLastFm,
    @SerializedName("published")
    val published: String,
    @SerializedName("summary")
    val summary: String
)