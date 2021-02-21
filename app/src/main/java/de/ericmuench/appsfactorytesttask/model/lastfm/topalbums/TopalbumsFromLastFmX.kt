package de.ericmuench.appsfactorytesttask.model.lastfm.topalbums


import com.google.gson.annotations.SerializedName

data class TopalbumsFromLastFmX(
    @SerializedName("album")
    val shortAlbumInfo: List<ShortAlbumInfoFromLastFm>,
    @SerializedName("@attr")
    val attr: AttrFromLastFm
)