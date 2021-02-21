package de.ericmuench.appsfactorytesttask.model.lastfm.artistinfo


import com.google.gson.annotations.SerializedName
import de.ericmuench.appsfactorytesttask.model.lastfm.image.ImageFromLastFm

data class ArtistFromLastFm(
    @SerializedName("bio")
    val bio: BioFromLastFm,
    @SerializedName("image")
    val image: List<ImageFromLastFm>,
    @SerializedName("mbid")
    val mbid: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("ontour")
    val ontour: String,
    @SerializedName("similar")
    val similar: SimilarFromLastFm,
    @SerializedName("stats")
    val stats: StatsFromLastFm,
    @SerializedName("streamable")
    val streamable: String,
    @SerializedName("tags")
    val tags: TagsFromLastFm,
    @SerializedName("url")
    val url: String
)