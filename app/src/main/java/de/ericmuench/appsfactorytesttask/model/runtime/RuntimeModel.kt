package de.ericmuench.appsfactorytesttask.model.runtime

data class Artist(
    val mbid : String,
    val artistName : String,
    var description : String,
    val onlineUrl : String?,
    val albums : List<Album>
){
    override fun equals(other: Any?): Boolean {
        if(other == null || other !is Artist){
            return false
        }

        return this.mbid == other.mbid && this.artistName == other.artistName
    }
}

data class Album(
    val title : String,
    val artists : List<Artist>,
    var description : String,
    val onlineUrl : String?,
    val imgUrl : String?,
    val songs: List<Song>
)

data class Song(
    val title: String,
    val onlineUrl: String?
)

data class ArtistSearchResults(
    val totalResults : Int,
    val startPage : Int,
    val startIndex : Int,
    val itemsPerPage : Int,
    val items: List<Artist>
)