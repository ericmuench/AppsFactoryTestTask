package de.ericmuench.appsfactorytesttask.model.room.dao

import androidx.room.*

@Dao
interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun mergeElement(element : T)

    @Delete
    fun deleteElement(element : T)
}