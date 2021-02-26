package de.ericmuench.appsfactorytesttask.model.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun mergeElement(element : T)

    @Delete
    suspend fun deleteElement(element : T)
}