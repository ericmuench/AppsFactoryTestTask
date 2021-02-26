package de.ericmuench.appsfactorytesttask.model.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

/**
 * This file contains all entity classes for storing data into the Room-Database.
 *
 * At the Moment the Model is like this:
 *          Artist to Albums --> 1:N
 *          Albums to Songs -->  N:M
 *          Artist to Songs -->  No direct relation
 *
 *          Songs and Artists should be deleted if Album is deleted.
 * */
@Entity(tableName = "artists")
data class StoredArtist(
    @PrimaryKey(autoGenerate = true) val arid: Int = 0,
    val mbid : String?,
    @ColumnInfo(name = "artist_name") val artistName : String,
    val description : String,
    @ColumnInfo(name = "online_url") val onlineUrl : String?,
    @ColumnInfo(name = "image_url") val imageUrl : String?,
)

@Entity(
    tableName = "albums",
    foreignKeys = [
        ForeignKey(
        entity = StoredArtist::class,
        parentColumns = arrayOf("arid"),
        childColumns = arrayOf("artist_id"))
    ]
)
data class StoredAlbum(
    @PrimaryKey(autoGenerate = true) val alid: Int = 0,
    val mbid : String?,
    val title : String,
    val description : String,
    @ColumnInfo(name = "online_url") val onlineUrl : String?,
    @ColumnInfo(name = "image_url") val imgUrl : String?,
    @ColumnInfo(name = "artist_id") val artistId : Int
)

@Entity(tableName = "songs")
data class StoredSong(
    @PrimaryKey(autoGenerate = true) val sid: Int = 0,
    val title: String,
    @ColumnInfo(name = "online_url") val onlineUrl: String?
)

@Entity(
    tableName = "album_songs",
    primaryKeys = ["song_id", "album_id"],
    foreignKeys = [
        ForeignKey(
            entity = StoredSong::class,
            parentColumns = arrayOf("sid"),
            childColumns = arrayOf("song_id"),
        ),
        ForeignKey(
            entity = StoredAlbum::class,
            parentColumns = arrayOf("alid"),
            childColumns = arrayOf("album_id"),
            onDelete = CASCADE
        )
    ]

)
data class StoredAlbumSongs(
    @ColumnInfo(name = "song_id") val songId : Int,
    @ColumnInfo(name = "album_id") val albumId : Int
)

