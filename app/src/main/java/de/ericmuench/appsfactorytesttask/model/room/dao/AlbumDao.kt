package de.ericmuench.appsfactorytesttask.model.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import de.ericmuench.appsfactorytesttask.model.room.StoredAlbum
import de.ericmuench.appsfactorytesttask.model.room.StoredAlbumSong
import de.ericmuench.appsfactorytesttask.model.runtime.Album

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
    abstract fun getAlbumById(id : Long) : List<StoredAlbum>

    @Query("SELECT * FROM albums;")
    abstract fun getAllAlbumsLiveData() : LiveData<List<StoredAlbum>>

    @Query("SELECT alid FROM albums WHERE title LIKE :albumTitle;")
    abstract fun getAlbumIdsByTitle(albumTitle : String) : List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun mergeAlbumSongs(albumSong : List<StoredAlbumSong>)

    @Query("""SELECT EXISTS(SELECT alid FROM albums INNER JOIN artists ON artists.arid == artist_id  
                   WHERE title LIKE :albumTitle AND artist_name LIKE :artistName);""")
    abstract fun isAlbumStoredByTitleAndArtistName(albumTitle : String,artistName : String) : Boolean
    //endregion

    //region Help Query-Functions
    @Query("Select MAX(alid) FROM albums;")
    abstract fun getMaxId() : Long
    //endregion
}