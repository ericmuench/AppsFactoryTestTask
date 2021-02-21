package de.ericmuench.appsfactorytesttask.model.lastfm.topalbums


import com.google.gson.annotations.SerializedName

data class AttrFromLastFm(
    @SerializedName("artist")
    val artist: String,
    @SerializedName("page")
    val page: String,
    @SerializedName("perPage")
    val perPage: String,
    @SerializedName("total")
    val total: String,
    @SerializedName("totalPages")
    val totalPages: String
)