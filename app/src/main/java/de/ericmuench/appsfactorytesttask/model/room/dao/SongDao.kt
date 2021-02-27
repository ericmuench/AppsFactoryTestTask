package de.ericmuench.appsfactorytesttask.model.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.ericmuench.appsfactorytesttask.model.room.StoredSong

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
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun mergeAll(songs : List<StoredSong>)

    @Query("SELECT * FROM songs WHERE sid IN (:idRange); ")
    abstract fun getAllInIdRange(idRange : List<Int>) : List<StoredSong>
    //endregion

    //region Help Query-Functions
    @Query("Select MAX(sid) FROM songs;")
    abstract fun getMaxId() : Long
    //endregion
}