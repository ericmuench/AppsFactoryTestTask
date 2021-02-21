package de.ericmuench.appsfactorytesttask.model.lastfm.artistinfo


import com.google.gson.annotations.SerializedName

data class LinkFromLastFm(
    @SerializedName("href")
    val href: String,
    @SerializedName("rel")
    val rel: String,
    @SerializedName("#text")
    val text: String
)