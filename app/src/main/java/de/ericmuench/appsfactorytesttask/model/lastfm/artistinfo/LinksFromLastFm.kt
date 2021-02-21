package de.ericmuench.appsfactorytesttask.model.lastfm.artistinfo


import com.google.gson.annotations.SerializedName

data class LinksFromLastFm(
    @SerializedName("link")
    val link: LinkFromLastFm
)