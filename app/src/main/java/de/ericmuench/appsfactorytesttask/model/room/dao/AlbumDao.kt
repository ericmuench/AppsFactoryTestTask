package de.ericmuench.appsfactorytesttask.model.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import de.ericmuench.appsfactorytesttask.model.room.StoredAlbum
import de.ericmuench.appsfactorytesttask.model.room.StoredAlbumSongs
import de.ericmuench.appsfactorytesttask.model.room.StoredArtist

/**This DAO should provide functionality for managing albums of the local Database*/
@Dao
interface AlbumDao : BaseDao<StoredAlbum>{
    @Query("SELECT * FROM albums;")
    fun getAllAlbumsLiveData() : LiveData<List<StoredAlbum>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun mergeAlbumSongs(albumSongs : StoredAlbumSongs)
}