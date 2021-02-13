package de.ericmuench.appsfactorytesttask.model.lastfm.artistsearch

import com.google.gson.annotations.SerializedName


//import com.google.gson.annotations.SerializedName

data class ArtistResultsFromLastFm(
    @SerializedName("artistmatches")
    val artistmatches: ArtistmatchesFromLastFm,
    @SerializedName("@attr")
    val attr: AttrFromLastFm,
    @SerializedName("opensearch:itemsPerPage")
    val opensearchItemsPerPage: String,
    @SerializedName("opensearch:Query")
    val opensearchQuery: OpensearchQueryFromLastFm,
    @SerializedName("opensearch:startIndex")
    val opensearchStartIndex: String,
    @SerializedName("opensearch:totalResults")
    val opensearchTotalResults: String
)