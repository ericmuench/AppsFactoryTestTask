package de.ericmuench.appsfactorytesttask.model.room

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import de.ericmuench.appsfactorytesttask.model.runtime.Album
import de.ericmuench.appsfactorytesttask.model.runtime.Artist
import de.ericmuench.appsfactorytesttask.model.runtime.Song

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
    @PrimaryKey val arid: Long,
    val mbid : String?,
    @ColumnInfo(name = "artist_name") val artistName : String,
    val description : String,
    @ColumnInfo(name = "online_url") val onlineUrl : String?,
    @ColumnInfo(name = "image_url") val imageUrl : String?,
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return when (other) {
            null -> false
            is StoredArtist -> {
                this.arid == other.arid
                        && this.mbid == other.mbid
                        && this.artistName == other.artistName
                        && this.description == other.description
                        && this.onlineUrl == other.onlineUrl
                        && this.imageUrl == other.imageUrl
            }
            is Artist -> {
                this.artistName == other.artistName
                        && this.description == other.description
                        && this.imageUrl == other.imageUrl
                        && this.mbid == other.mbid
                        && this.onlineUrl == other.onlineUrl
            }
            else -> false
        }
    }

    override fun hashCode(): Int {
        var result = arid.hashCode()
        result = 31 * result + (mbid?.hashCode() ?: 0)
        result = 31 * result + artistName.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + (onlineUrl?.hashCode() ?: 0)
        result = 31 * result + (imageUrl?.hashCode() ?: 0)
        return result
    }
}

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
    @PrimaryKey val alid: Long,
    val mbid : String?,
    val title : String,
    val description : String,
    @ColumnInfo(name = "online_url") val onlineUrl : String?,
    @ColumnInfo(name = "image_url") val imgUrl : String?,
    @ColumnInfo(name = "artist_id") val artistId : Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return when(other){
            null -> false
            is StoredAlbum-> {
                alid == other.alid
                        && mbid == other.mbid
                        && title == other.title
                        && description == other.description
                        && onlineUrl == other.onlineUrl
                        && imgUrl == other.imgUrl
                        && artistId == other.artistId
            }
            is Album -> {
                mbid == other.mbid
                        && title == other.title
                        && description == other.description
                        && onlineUrl == other.onlineUrl
                        && imgUrl == other.imgUrl
            }
            else -> false
        }
    }

    override fun hashCode(): Int {
        var result = alid.hashCode()
        result = 31 * result + (mbid?.hashCode() ?: 0)
        result = 31 * result + title.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + (onlineUrl?.hashCode() ?: 0)
        result = 31 * result + (imgUrl?.hashCode() ?: 0)
        result = 31 * result + artistId.hashCode()
        return result
    }
}

@Entity(tableName = "songs")
data class StoredSong(
    @PrimaryKey val sid: Long,
    val title: String,
    @ColumnInfo(name = "online_url") val onlineUrl: String?
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return when(other){
            null -> false
            is StoredSong -> {
                this.sid == other.sid
                        && this.onlineUrl == other.onlineUrl
                        && this.title == other.title
            }
            is Song -> this.title == other.title && this.onlineUrl == other.onlineUrl
            else -> false
        }
    }

    override fun hashCode(): Int {
        var result = sid.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + (onlineUrl?.hashCode() ?: 0)
        return result
    }
}

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
data class StoredAlbumSong(
    @ColumnInfo(name = "song_id") val songId : Long,
    @ColumnInfo(name = "album_id") val albumId : Long
)


data class StoredAlbumInfo(
    @PrimaryKey val alid: Long,
    val title : String,
    @ColumnInfo(name = "image_url") val imgUrl : String?,
    @ColumnInfo(name = "artist_name") val artistName : String
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StoredAlbumInfo) return false

        if (alid != other.alid) return false
        if (title != other.title) return false
        if (imgUrl != other.imgUrl) return false
        if (artistName != other.artistName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = alid.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + (imgUrl?.hashCode() ?: 0)
        result = 31 * result + artistName.hashCode()
        return result
    }
}
