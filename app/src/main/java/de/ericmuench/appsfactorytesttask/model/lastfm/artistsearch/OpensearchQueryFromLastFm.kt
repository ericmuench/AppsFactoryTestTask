package de.ericmuench.appsfactorytesttask.model.lastfm.artistsearch


import com.google.gson.annotations.SerializedName

data class OpensearchQueryFromLastFm(
    @SerializedName("role")
    val role: String,
    @SerializedName("searchTerms")
    val searchTerms: String,
    @SerializedName("startPage")
    val startPage: String,
    @SerializedName("#text")
    val text: String
)