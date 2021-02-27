package de.ericmuench.appsfactorytesttask.model.room.dao

import androidx.room.*
import de.ericmuench.appsfactorytesttask.model.room.StoredSong
import de.ericmuench.appsfactorytesttask.model.runtime.Song

@Dao
/**
 * This interface provides functionality for managing Songs
 * */
abstract class SongDao : BaseDao<StoredSong>{

    //region Fields
    private val idGeneratorSequence = sequence<Long> {
        var maxId = getMaxId().takeIf { it >= 0 } ?: 0
        while(true){
            yield(++maxId)
        }
    }

    val idGenerator = idGeneratorSequence.iterator()
    //endregion

    //region Functions
    /**
     * This function takes a List of Tuples of StoredSong and Boolean. If the Boolean is TRUE then
     * the associated StoredSong does already exist and needs to be updated, otherwise it needs to
     * be inserted.
     * */
    fun mergeAll(songs : Iterable<Pair<StoredSong,Boolean>>){
        songs.forEach {
            val isAlreadyInDatabase = it.second
            if(isAlreadyInDatabase){
                updateSong(it.first.sid,it.first.title,it.first.onlineUrl)
            }
            else{
                insertElement(it.first)
            }
        }
    }

    @Query("SELECT * FROM songs WHERE sid IN (:idRange); ")
    abstract fun getAllInIdRange(idRange : List<Int>) : List<StoredSong>

    @Query("""SELECT DISTINCT sid FROM songs 
                   INNER JOIN album_songs ON album_songs.song_id == songs.sid 
                   INNER JOIN albums ON album_songs.album_id == albums.alid
                   WHERE songs.title LIKE :title AND albums.artist_id == :artistId;""")
    abstract fun getSongIdForTitleAndArtist(title: String, artistId : Long) : List<Long>

    @Query("SELECT * FROM songs WHERE sid == :songId;")
    abstract fun getSongById(songId: Long) : StoredSong?

    @Query("UPDATE songs SET title = :title, online_url = :onlineUrl WHERE sid == :songId;")
    abstract fun updateSong(songId: Long, title: String, onlineUrl : String?)

    @Query("SELECT DISTINCT song_id FROM album_songs WHERE album_id == :albumId;")
    abstract fun getSongIdsByAlbumId(albumId : Long) : List<Long>

    @Query("""SELECT DISTINCT songs.sid, songs.title, songs.online_url 
                   FROM album_songs JOIN songs ON album_songs.song_id == songs.sid
                   WHERE album_songs.album_id == :albumId;""")
    abstract fun getSongsByAlbumId(albumId :Long) : List<StoredSong>

    @Query("DELETE FROM songs;")
    abstract fun deleteAllSongs()

    @Query("""DELETE FROM songs WHERE sid in (:songIds) 
                   AND NOT EXISTS (SELECT song_id FROM album_songs WHERE song_id == sid);""")
    abstract fun deleteSongsWithoutAlbumAndIds(songIds : List<Long>)
    //endregion

    //region Help Query-Functions
    @Query("Select MAX(sid) FROM songs;")
    abstract fun getMaxId() : Long
    //endregion
}