package de.ericmuench.appsfactorytesttask.model.lastfm.artistinfo


import com.google.gson.annotations.SerializedName

data class StatsFromLastFm(
    @SerializedName("listeners")
    val listeners: String,
    @SerializedName("playcount")
    val playcount: String
)