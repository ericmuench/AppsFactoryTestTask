package de.ericmuench.appsfactorytesttask.model.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.ericmuench.appsfactorytesttask.model.room.dao.AlbumDao
import de.ericmuench.appsfactorytesttask.model.room.dao.ArtistDao
import de.ericmuench.appsfactorytesttask.model.room.dao.SongDao

@Database(
    entities = [StoredAlbum::class, StoredSong::class, StoredArtist::class, StoredAlbumSong::class],
    version = 1, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    //region Java Style Singleton
    companion object{
        private lateinit var instance: AppDatabase

        fun getInstance(context: Context): AppDatabase{
            if (!::instance.isInitialized) {
                instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "af_test_task_db")
                    .build()
            }

            return instance
        }

    }
    //endregion

    //region DAO Functions
    abstract fun albumDao() : AlbumDao
    abstract fun artistDao() : ArtistDao
    abstract fun songDao() : SongDao
    //endregion

}