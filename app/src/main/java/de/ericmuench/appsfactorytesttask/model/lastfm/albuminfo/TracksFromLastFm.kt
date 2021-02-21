package de.ericmuench.appsfactorytesttask.model.lastfm.albuminfo


import com.google.gson.annotations.SerializedName

data class TracksFromLastFm(
    @SerializedName("track")
    val track: List<TrackFromLastFm>
)