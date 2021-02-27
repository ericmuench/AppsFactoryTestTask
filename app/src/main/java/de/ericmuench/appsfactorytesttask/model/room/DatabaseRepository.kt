package de.ericmuench.appsfactorytesttask.model.room

import android.content.Context
import de.ericmuench.appsfactorytesttask.clerk.mapper.RuntimeModelToDatabaseMapper
import de.ericmuench.appsfactorytesttask.model.runtime.Album
import de.ericmuench.appsfactorytesttask.model.runtime.Artist
import de.ericmuench.appsfactorytesttask.model.runtime.Song
import de.ericmuench.appsfactorytesttask.model.runtime.repository.DataRepositoryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class DatabaseRepository(context : Context) {
    //region fields
    private val appDatabase = AppDatabase.getInstance(context)

    private val albumDao = appDatabase.albumDao()
    private val artistDao = appDatabase.artistDao()
    private val songDao = appDatabase.songDao()

    private val dataMapper = RuntimeModelToDatabaseMapper()
    //endregion

    //region functions
    /**
     * This function checks whether a certain Album is stored in the Database.
     * @param album The album to be checked for existence in the database.
     *
     * @return Whether the album is stored in the database or not or an Error.
     * */
    suspend fun isAlbumStored(album: Album) : DataRepositoryResponse<Boolean, Throwable> = coroutineScope{
        return@coroutineScope try {
            val isStoredDef = async(Dispatchers.IO){
                albumDao.isAlbumStoredByTitleAndArtistName(album.title,album.artistName)
            }

            DataRepositoryResponse.Data(isStoredDef.await())
        }
        catch(ex : Exception){
            DataRepositoryResponse.Error(ex)
        }
    }

    /**
     * This function can store an Album in the Database. Be aware that a Merge-Strategy is used,
     * meaning that existing elements might be overridden. Additionally to adding the album to the
     * Database, Artist and Song might also be added to the Database in context of a certain album.
     *
     * @param album The album to be stored
     * @param artist A corresponding Artist for the album
     *
     * @return A DataRepositoryResponse with either a success-indication or an Error that the store-
     * operation failed. False as a return type can be used in future implementations to maybe indicate
     * some other events.
     * */
    suspend fun storeAlbumWithAssociatedData(
        album: Album,
        artist : Artist
    ) : DataRepositoryResponse<Boolean,Throwable> = coroutineScope {
        val responseDef  = async(Dispatchers.IO){
            try {
                //artist
                val artistId = getStoredArtistIdByName(artist.artistName)
                    ?: storeArtist(artist) //artistId is null --> need to store the artist and then return its id again
                    ?: return@async DataRepositoryResponse.Error(Exception("Unable to insert Artist into Database"))

                //album
                val albumId = storeAlbum(album, artistId)
                    ?: return@async DataRepositoryResponse.Error(Exception("Album Insert failed"))


                //link songs
                val storedSongIds = storeSongs(album.songs)
                    ?: return@async DataRepositoryResponse.Error(Exception("Song Insert failed"))

                val albumSongs = dataMapper.mapRelationAlbumSongWithIds(storedSongIds,albumId)
                albumDao.mergeAlbumSongs(albumSongs)

                DataRepositoryResponse.Data(true)
            }
            catch (ex: Exception){
                DataRepositoryResponse.Error(ex)
            }
        }

        responseDef.await()
    }
    //endregion

    //region Help functions
    /**
     * This function returns an ID of an Artist where the name matches the given artistName.
     * @param artistName The name of the artist to get the ID for
     *
     * @return If multiple results match or no results are found, the function returns null. Else,
     * the corresponding Artist-ID is returned.
     * */
    private suspend fun getStoredArtistIdByName(artistName : String) : Long? = coroutineScope{
        val storedArtistDeferred = async(Dispatchers.IO){
            artistDao
                .getArtistIdsByName(artistName)
                .takeIf { it.size == 1 }
                ?.first()
        }

        return@coroutineScope storedArtistDeferred.await()
    }

    /**
     * This function is capable of storing an Artist in the Database.
     * @param artist The artist to store in the Database
     *
     * @return The ID of the artist stored in the Database or null if storing failed
     * */
    private suspend fun storeArtist(artist: Artist) : Long? = coroutineScope{
        val artistIdDef = async(Dispatchers.IO){
            val idFromGenerator = artistDao.idGenerator.next()
            val storedArtist = dataMapper.mapArtist(artist,idFromGenerator)
            artistDao.mergeElement(storedArtist)
            //perform check --> only return generated id if the object fetched with that Id equals
            //the artist to be inserted
            val checkFetch = artistDao.getArtistsById(idFromGenerator)
            return@async idFromGenerator.takeIf {
                checkFetch.size == 1 && checkFetch.first().equals(artist)
            }
        }

        return@coroutineScope artistIdDef.await()
    }

    /**This function work similar to storeArtist but for an Album*/
    private suspend fun storeAlbum(album : Album,artistId : Long) : Long? = coroutineScope {
        val albumIdDef = async(Dispatchers.IO){
            val idFromGenerator = albumDao.idGenerator.next()
            val storedAlbum = dataMapper.mapAlbum(album,idFromGenerator,artistId)
            albumDao.mergeElement(storedAlbum)
            //perform check --> only return generated id if the object fetched with that Id equals
            //the album to be inserted and the artist matches
            val checkFetch = albumDao.getAlbumById(idFromGenerator)
            return@async idFromGenerator.takeIf {
                val albumMatch = checkFetch.size == 1 && checkFetch.first().equals(album)
                val artistsForCheckFetch = artistDao.getArtistsById(checkFetch.first().artistId)
                val artistMatch = artistsForCheckFetch.size == 1
                        && artistsForCheckFetch.first().arid == artistId

                albumMatch && artistMatch
            }
        }

        return@coroutineScope albumIdDef.await()
    }

    /**
     * This function is similar to storeArtist but for Songs
     *
     * TODO: Maybe implement check-fetch later
     * */
    private suspend fun storeSongs(songs : Iterable<Song>) : List<Long>? = coroutineScope{
        val stSongs = songs.map {
            dataMapper.mapSong(it,songDao.idGenerator.next())
        }
        songDao.mergeAll(stSongs)

        return@coroutineScope stSongs.map { it.sid }
    }

    /**This function works similar to getStoredArtistIdByName but for Albums*/
    //TODO: Delete this function
    private suspend fun getAlbumIdByTitle(albumTitle : String) : Long? = coroutineScope{
        val albumIdDef = async(Dispatchers.IO){
            albumDao.getAlbumIdsByTitle(albumTitle).takeIf { it.size == 1 }?.first()
        }

        return@coroutineScope albumIdDef.await()
    }
    //endregion
}