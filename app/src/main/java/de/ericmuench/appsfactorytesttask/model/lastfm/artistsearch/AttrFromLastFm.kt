package de.ericmuench.appsfactorytesttask.model.lastfm.artistsearch


import com.google.gson.annotations.SerializedName

data class AttrFromLastFm(
    @SerializedName("for")
    val forX: String
)