package de.ericmuench.appsfactorytesttask.clerk.mapper

import de.ericmuench.appsfactorytesttask.model.room.StoredAlbum
import de.ericmuench.appsfactorytesttask.model.room.StoredAlbumSong
import de.ericmuench.appsfactorytesttask.model.room.StoredArtist
import de.ericmuench.appsfactorytesttask.model.room.StoredSong
import de.ericmuench.appsfactorytesttask.model.runtime.Album
import de.ericmuench.appsfactorytesttask.model.runtime.Artist
import de.ericmuench.appsfactorytesttask.model.runtime.Song

/**
 * This class should map Runtime-Model-classes to classes that are used for the Apps Database.
 * */
class RuntimeModelToDatabaseMapper {

    //region Functions
    fun mapSong(song : Song,id: Long) : StoredSong = StoredSong(
        sid = id,
        title = song.title,
        onlineUrl = song.onlineUrl
    )

    fun mapArtist(artist : Artist,id: Long) : StoredArtist = StoredArtist(
        arid = id,
        mbid = artist.mbid,
        artistName = artist.artistName,
        description = artist.description,
        onlineUrl = artist.onlineUrl,
        imageUrl = artist.imageUrl
    )

    fun mapAlbum(album: Album, albumId: Long, artistId : Long) : StoredAlbum = StoredAlbum(
        alid = albumId,
        mbid = album.mbid,
        title = album.title,
        description = album.description,
        onlineUrl = album.onlineUrl,
        imgUrl = album.imgUrl,
        artistId = artistId
    )

    fun mapAlbum(album : Album, albumId: Long, artist: StoredArtist) : StoredAlbum
        = mapAlbum(album,albumId,artist.arid)

    fun mapRelationAlbumSong(songs : List<StoredSong>, albumId : Long) : List<StoredAlbumSong>{
        return mapRelationAlbumSongWithIds(songs.map { it.sid },albumId)
    }

    fun mapRelationAlbumSongWithIds(songIds : List<Long>, albumId : Long) : List<StoredAlbumSong>{
        return songIds.map {
            StoredAlbumSong(
                songId = it,
                albumId = albumId
            )
        }
    }

    //endregion
}