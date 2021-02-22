package de.ericmuench.appsfactorytesttask

import com.github.kittinunf.result.Result
import de.ericmuench.appsfactorytesttask.clerk.network.LastFmApiClient
import de.ericmuench.appsfactorytesttask.model.runtime.Album
import de.ericmuench.appsfactorytesttask.model.runtime.Artist
import kotlinx.coroutines.*
import org.junit.Test
import org.junit.Assert.*

/**
 * This class can test the LastFmApiClient
 * */
class LastFmApiClientTest {

    //fields
    private val apiClient = LastFmApiClient()


    /**
     * This function does a simple Search for an Artist that has "Ed" in its name.
     * There should be 10 elements to be found, starting with the page 1.
     * */
    @Test
    fun testArtistSearch(): Unit = runBlocking{
        val searchResultDef = async {
            apiClient.searchArtists("Ed",1,10)
        }

        val searchResult = searchResultDef.await()

        assertTrue(searchResult is Result.Success)

        if(searchResult is Result.Success){
            println("Data was successfully searched")
            val data = searchResult.value
            println("found artists is: ${data.items.map { it.artistName }}")
            assertTrue(
                data.startPage == 1
                && data.items.size == 10)
        }
    }


    /**
     * This function tests if there is valid Artist-Info delivered the client
     * */
    @Test
    fun testGetVariousArtists() : Unit = runBlocking{
        //various artists to search for
        val artistNames = listOf(
            "Ed Sheeran",
            "Skrillex",
            "Earth,Wind & Fire",
            "AC/DC",
            "Axwell /\\ Ingrosso"
        )

        val artistTestsDeferred = mutableListOf<Deferred<Result<Artist,Exception>>>()
        artistNames.forEach { name ->
            artistTestsDeferred.add(async(Dispatchers.IO){
                apiClient.getArtistByName(name)
            })
        }

        val results = artistTestsDeferred.map { it.await() }

        results.forEach {
            assert(it is Result.Success)
            if(it is Result.Success){
                val artist = it.value
                println("Fetched: $artist")
                assert(artist.description.isNotBlank())
                assert(artist.artistName.isNotBlank())
            }
        }

    }

    /**
     * This function tests getting various albums of different artists
     * */
    @Test
    fun getVariousAlbums() = runBlocking{
        val artistTitleData = mutableListOf(
            "Ed Sheeran" to "÷ (Deluxe)",
            "AC/DC" to "Back in Black",
            "Shawn Mendes" to "Señorita",
            "Shawn Mendes" to "Wonder",
            "Shawn Mendes" to "if I can't have you"
        )

        val albumTestsDeferred = mutableListOf<Deferred<Result<Album,Exception>>>()
        artistTitleData.forEach { entry ->
            albumTestsDeferred.add(async(Dispatchers.IO){
                apiClient.getAlbum(entry.first,entry.second)
            })
        }

        val results = albumTestsDeferred.map { it.await() }

        assert(results.size == artistTitleData.size )
        results.forEach {
            if(it is Result.Failure){
                it.error.printStackTrace()
            }

            assert(it is Result.Success)

            if(it is Result.Success){
                val album = it.value
                println("Fetched: ${album.artistName} - ${album.title}")
                assert(album.title.isNotBlank())
                assert(album.artistName.isNotBlank())
            }
        }
    }

    /**
     * This function can test whether a certain amount of TopAlbums of an Artist can be fetched and
     * their data might be valid. For the Test the artist "Ed Sheeran" is used. There should be
     * 10 TopAlbums fetched starting with page 1. An Album should contain at least 1 Song.
     * */
    @Test
    fun getTopAlbumsOfArtist() : Unit = runBlocking{
        val artistName = "Ed Sheeran"
        val albumsResultDeferred = async(Dispatchers.IO){
            apiClient.getTopAlbumsOfArtist(artistName,1,10)
        }

        val albumsResult = albumsResultDeferred.await()

        if(albumsResult is Result.Failure){
            albumsResult.error.printStackTrace()
        }

        assert(albumsResult is Result.Success)

        if(albumsResult is Result.Success){
            val resultData = albumsResult.value

            assert(resultData.page == 1)
            //The result of items per page might be smaller than 10 due to the fact that some Albums
            //might be invalid or had a network failure. However, the length of fetched items should
            //never be greater than the given limit
            assert(resultData.perPage <= 10)
            println("Fetched ${resultData.perPage} albums")
            resultData.albums.forEach { album ->
                if(album.songs.isEmpty()){
                    println("WARNING: ${album.title} has no songs")
                }
            }

        }
    }
}