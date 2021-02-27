package de.ericmuench.appsfactorytesttask.model.room.dao

import androidx.room.*
import de.ericmuench.appsfactorytesttask.model.room.StoredArtist

/**This DAO should provide functionality for managing artists of the local Database*/
@Dao
abstract class ArtistDao : BaseDao<StoredArtist> {

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
    @Query("SELECT arid FROM artists WHERE artist_name LIKE :artistName;")
    abstract fun getArtistIdsByName(artistName : String) : List<Long>

    @Query("SELECT * FROM artists WHERE arid == :id;")
    abstract fun getArtistsById(id : Long) : List<StoredArtist>
    //endregion

    //region Help Query-Functions
    @Query("Select MAX(arid) FROM artists;")
    abstract fun getMaxId() : Long
    //endregion
}