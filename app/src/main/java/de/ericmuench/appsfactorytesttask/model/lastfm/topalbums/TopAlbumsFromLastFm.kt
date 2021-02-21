package de.ericmuench.appsfactorytesttask.model.lastfm.topalbums


import com.google.gson.annotations.SerializedName

data class TopAlbumsFromLastFm(
    @SerializedName("topalbums")
    val topalbums: TopalbumsFromLastFmX
)