package de.ericmuench.appsfactorytesttask.model.lastfm.artistinfo


import com.google.gson.annotations.SerializedName

data class TagsFromLastFm(
    @SerializedName("tag")
    val tag: List<TagFromLastFm>
)