package de.ericmuench.appsfactorytesttask.model.room.dao

import androidx.room.*

@Dao
interface BaseDao<T> {
    @Insert
    fun insertElement(element : T)

    @Delete
    fun deleteElement(element : T)
}