package de.ericmuench.appsfactorytesttask.model.room

import android.content.Context

class DatabaseRepository(context : Context) {
    //region fields
    private val appDatabase = AppDatabase.getInstance(context)
    //endregion
}