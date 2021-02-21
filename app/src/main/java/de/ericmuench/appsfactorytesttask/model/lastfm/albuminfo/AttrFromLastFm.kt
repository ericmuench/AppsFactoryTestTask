package de.ericmuench.appsfactorytesttask.model.lastfm.albuminfo


import com.google.gson.annotations.SerializedName

data class AttrFromLastFm(
    @SerializedName("rank")
    val rank: String
)