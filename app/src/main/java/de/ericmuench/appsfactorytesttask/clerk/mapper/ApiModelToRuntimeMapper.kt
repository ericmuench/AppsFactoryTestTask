package de.ericmuench.appsfactorytesttask.clerk.mapper

import de.ericmuench.appsfactorytesttask.model.lastfm.albuminfo.AlbumInfoFromLastFm
import de.ericmuench.appsfactorytesttask.model.lastfm.artistinfo.ArtistInfoFromLastFm
import de.ericmuench.appsfactorytesttask.model.lastfm.artistsearch.ArtistSearchResultFromLastFm
import de.ericmuench.appsfactorytesttask.model.lastfm.image.ImageFromLastFm
import de.ericmuench.appsfactorytesttask.model.lastfm.artistsearch.SearchedArtistFromLastFm
import de.ericmuench.appsfactorytesttask.model.lastfm.error.ErrorFromLastFm
import de.ericmuench.appsfactorytesttask.model.lastfm.error.LastFmException
import de.ericmuench.appsfactorytesttask.model.lastfm.topalbums.TopAlbumsFromLastFm
import de.ericmuench.appsfactorytesttask.model.runtime.*
import java.util.*

/**
 * This class should map ApiTypes from LastFM to classes in Runtime Model
 */
class ApiModelToRuntimeMapper {

    //region functions for artist search
    fun mapArtistSearchResults(apiSearchRes : ArtistSearchResultFromLastFm)
        : ArtistSearchResult = with(apiSearchRes.artistResults){
        return@with ArtistSearchResult(
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
        imageUrl = getImageUrlFromLastFmImageList("medium",apiArtist.image)
    )
    //endregion

    //region functions for albums
    fun mapAlbumInfo(apiAlbumInfo : AlbumInfoFromLastFm, artistName: String) : Album {
        return Album(
            mbid = apiAlbumInfo.album.mbid,
            title = apiAlbumInfo.album.name,
            description = apiAlbumInfo.album.wiki?.summary ?: "",
            imgUrl = getImageUrlFromLastFmImageList("extralarge",apiAlbumInfo.album.image),
            onlineUrl = apiAlbumInfo.album.url,
            artistName = artistName,
            songs = apiAlbumInfo.album.tracks.track.map {
                Song(it.name,it.url)
            }
        )
    }

    fun mapTopAlbumsResult(
        apiTopAlbums : TopAlbumsFromLastFm,
        albums : List<Album> = emptyList()
    ) : TopAlbumOfArtistResult = TopAlbumOfArtistResult(
        albums= albums,
        page = apiTopAlbums.topalbums.attr.page.toInt(),
        totalPages = apiTopAlbums.topalbums.attr.totalPages.toInt(),
        perPage = albums.size
    )
    //endregion

    //region functions for Artists
    fun mapArtistInfo(apiArtistInfo: ArtistInfoFromLastFm) : Artist = Artist(
        mbid = apiArtistInfo.artist.mbid,
        artistName = apiArtistInfo.artist.name,
        description = apiArtistInfo.artist.bio.summary.trim(),
        onlineUrl = apiArtistInfo.artist.url,
        imageUrl = getImageUrlFromLastFmImageList("medium",apiArtistInfo.artist.image)
    )
    //endregion


    //functions for Errors
    fun mapError(apiError : ErrorFromLastFm): Exception = apiError.getLastFmException()

    //help functions
    /**
     * This function can be used to get an Image-URL out of an List of LastFm-Images. You can
     * specify a preferred image size. If this one is not available, then the first image in the
     * image List is chosen. If the list is empty, null will be returned
     *
     * @param preference The preferred image size
     * @param images The list of images to choose the image from
     *
     * @return The Image URL for the preferred sized image, the alternative image url or null
     * */
    private fun getImageUrlFromLastFmImageList(preference : String, images : List<ImageFromLastFm>) : String?{
        if(images.isEmpty()){
            return null
        }

        return images
            .filter { it.size.trim().equals(preference.trim(), ignoreCase = true) }
            .takeIf { it.isNotEmpty() }
            ?.get(0)
            ?.text
            ?: images[0].text
    }
}