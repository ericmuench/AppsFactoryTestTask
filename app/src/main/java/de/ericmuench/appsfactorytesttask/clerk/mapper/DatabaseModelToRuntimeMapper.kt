package de.ericmuench.appsfactorytesttask.clerk.mapper

import de.ericmuench.appsfactorytesttask.model.room.StoredAlbum
import de.ericmuench.appsfactorytesttask.model.room.StoredSong
import de.ericmuench.appsfactorytesttask.model.runtime.Album
import de.ericmuench.appsfactorytesttask.model.runtime.Song

/**
 * This function maps classes from the Room-Model to Classes from the RuntimeModel
 * */
class DatabaseModelToRuntimeMapper {

    //region Functions
    fun mapSong(dbSong : StoredSong) : Song = Song(
        title = dbSong.title,
        onlineUrl = dbSong.onlineUrl
    )

    fun mapAlbumWithRuntimeSongs(dbAlbum : StoredAlbum, artistName : String, songs: List<Song>) : Album = Album(
        mbid = dbAlbum.mbid,
        title = dbAlbum.title,
        description = dbAlbum.description,
        onlineUrl = dbAlbum.onlineUrl,
        imgUrl = dbAlbum.imgUrl,
        artistName = artistName,
        songs = songs
    )


    fun mapAlbum(dbAlbum : StoredAlbum, artistName : String, songs: List<StoredSong>) : Album{
        return  mapAlbumWithRuntimeSongs(dbAlbum,artistName,songs.map { mapSong(it) })
    }
    //endregion
}