package de.ericmuench.appsfactorytesttask.clerk.mapper

import de.ericmuench.appsfactorytesttask.model.lastfm.artistsearch.ArtistSearchResultFromLastFm
import de.ericmuench.appsfactorytesttask.model.lastfm.artistsearch.SearchedArtistFromLastFm
import de.ericmuench.appsfactorytesttask.model.lastfm.error.ErrorFromLastFm
import de.ericmuench.appsfactorytesttask.model.lastfm.error.ExtendedErrorFromLastFm
import de.ericmuench.appsfactorytesttask.model.runtime.Artist
import de.ericmuench.appsfactorytesttask.model.runtime.LastFmArtistSearchResults

/**
 * This class should map ApiTypes from LastFM to classes in Runtime Model
 */
class ApiModelToRuntimeMapper {

    //functions for artist search
    fun mapArtistSearchResults(apiSearchRes : ArtistSearchResultFromLastFm)
        : LastFmArtistSearchResults = with(apiSearchRes.artistResults){
        return@with LastFmArtistSearchResults(
            opensearchTotalResults.toInt(),
            opensearchQuery.startPage.toInt(),
            opensearchStartIndex.toInt(),
            opensearchItemsPerPage.toInt(),
            mapSearchedArtists(artistmatches.searchedArtist).toList()
        )
    }

    fun mapSearchedArtists(apiArtists : Iterable<SearchedArtistFromLastFm>) : Iterable<Artist>
        = apiArtists.map {
            mapSearchedArtist(it)
        }

    fun mapSearchedArtist(apiArtist : SearchedArtistFromLastFm) : Artist = Artist(
        mbid = apiArtist.mbid,
        artistName = apiArtist.name,
        description = "",
        onlineUrl = apiArtist.url,
        albums = emptyList()
    )


    //functions for Errors
    fun mapError(apiError : ErrorFromLastFm): Exception = Exception(apiError.getLastFmErrorMessage())
}