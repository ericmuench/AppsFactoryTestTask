package de.ericmuench.appsfactorytesttask.model.room.dao

import androidx.room.*
import de.ericmuench.appsfactorytesttask.model.room.StoredArtist

/**This DAO should provide functionality for managing artists of the local Database*/
@Dao
interface ArtistDao : BaseDao<StoredArtist> {

    @Query("SELECT arid FROM artists WHERE artist_name LIKE :artistName;")
    suspend fun getArtistIdsByName(artistName : String) : List<Int>

    @Query("SELECT * FROM artists WHERE arid == :id;")
    suspend fun getArtistsById(id : Int) : List<StoredArtist>

}