package de.ericmuench.appsfactorytesttask.model.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import de.ericmuench.appsfactorytesttask.model.room.StoredAlbum
import de.ericmuench.appsfactorytesttask.model.room.StoredAlbumInfo
import de.ericmuench.appsfactorytesttask.model.room.StoredAlbumSong

/**This DAO should provide functionality for managing albums of the local Database*/
@Dao
abstract class AlbumDao : BaseDao<StoredAlbum>{

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
    @Query("SELECT * FROM albums WHERE alid == :id;")
    abstract fun getAlbumById(id : Long) : StoredAlbum?

    @Query("""SELECT DISTINCT albums.alid, albums.title, albums.image_url ,artists.artist_name
                   FROM albums JOIN artists ON albums.artist_id == artists.arid
                   ORDER BY albums.alid DESC;""")
    abstract fun getAllAlbumsLiveData() : LiveData<List<StoredAlbumInfo>>

    @Query("SELECT alid FROM albums WHERE title LIKE :albumTitle;")
    abstract fun getAlbumIdsByTitle(albumTitle : String) : List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun mergeAlbumSongs(albumSong : List<StoredAlbumSong>)

    @Query("""SELECT EXISTS(SELECT alid FROM albums INNER JOIN artists ON artists.arid == artist_id  
                   WHERE title LIKE :albumTitle AND artist_name LIKE :artistName);""")
    abstract fun isAlbumStoredByTitleAndArtistName(albumTitle : String,artistName : String) : Boolean

    @Query("""SELECT DISTINCT alid FROM albums INNER JOIN artists ON artists.arid == artist_id
                   WHERE albums.title LIKE :albumTitle AND artists.artist_name LIKE :artistName;""")
    abstract fun getAlbumIdByTitleAndArtistName(albumTitle : String,artistName : String) : List<Long>

    @Query("""SELECT DISTINCT alid FROM albums 
                   WHERE title LIKE :albumTitle AND artist_id == :artistId;""")
    abstract fun getAlbumIdByTitleAndArtistId(albumTitle : String,artistId : Long) : List<Long>

    @Query("""SELECT alid FROM albums WHERE 
                    title LIKE :title AND 
                    mbid LIKE :mbid AND 
                    description LIKE :description AND
                    online_url LIKE :onlineUrl AND
                    image_url LIKE :imgUrl;"""
    )
    abstract fun getAlbumIdsByParams(
        title : String,
        mbid : String?,
        description : String,
        onlineUrl: String?,
        imgUrl: String?
    ) : List<Long>

    @Query("SELECT EXISTS(SELECT alid FROM albums WHERE artist_id == :artistId);")
    abstract fun artistHasAlbums(artistId: Long) : Boolean


    @Query("DELETE FROM albums WHERE alid == :albumId;")
    abstract fun deleteElementByAlbumId(albumId : Long)
    //endregion

    //region Help Query-Functions
    @Query("Select MAX(alid) FROM albums;")
    abstract fun getMaxId() : Long
    //endregion
}