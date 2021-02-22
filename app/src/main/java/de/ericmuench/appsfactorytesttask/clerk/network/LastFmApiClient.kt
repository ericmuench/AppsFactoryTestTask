package de.ericmuench.appsfactorytesttask.clerk.network

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.flatMap
import com.github.kittinunf.result.map
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.ericmuench.appsfactorytesttask.BuildConfig
import de.ericmuench.appsfactorytesttask.clerk.mapper.ApiModelToRuntimeMapper
import de.ericmuench.appsfactorytesttask.model.lastfm.albuminfo.AlbumInfoFromLastFm
import de.ericmuench.appsfactorytesttask.model.lastfm.artistinfo.ArtistInfoFromLastFm
import de.ericmuench.appsfactorytesttask.model.lastfm.artistsearch.ArtistSearchResultFromLastFm
import de.ericmuench.appsfactorytesttask.model.lastfm.error.ErrorFromLastFm
import de.ericmuench.appsfactorytesttask.model.lastfm.error.ExtendedErrorFromLastFm
import de.ericmuench.appsfactorytesttask.model.lastfm.topalbums.TopAlbumsFromLastFm
import de.ericmuench.appsfactorytesttask.model.runtime.Album
import de.ericmuench.appsfactorytesttask.model.runtime.Artist
import de.ericmuench.appsfactorytesttask.model.runtime.ArtistSearchResult
import de.ericmuench.appsfactorytesttask.model.runtime.TopAlbumOfArtistResult
import de.ericmuench.appsfactorytesttask.util.formatter.UrlParameterFormatter
import de.ericmuench.appsfactorytesttask.util.json.GsonPropertyChecker
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope


/**
 * This class is responsible for all API-Calls to LastFm-API
 *
 * TODO: further documentation
 */
class LastFmApiClient {
    //companion
    companion object{
        private const val LAST_FM_API_BASE_URL = "https://ws.audioscrobbler.com/2.0/"
    }

    //fields
    private val paramFormat = UrlParameterFormatter()
    private val gson = Gson()
    private val dataMapper = ApiModelToRuntimeMapper()
    private val jsonPropCheck = GsonPropertyChecker()


    //functions
    suspend fun searchArtists(searchQuery: String, startPage: Int, elementsPerPage: Int)
        : Result<ArtistSearchResult, Exception> = coroutineScope {
        val url = buildUrl(
            "artist.search",
            Pair("artist",searchQuery),
            Pair("page","$startPage"),
            Pair("limit","$elementsPerPage")
        )

        return@coroutineScope try{
            getResultFromApi<ArtistSearchResultFromLastFm, ExtendedErrorFromLastFm>(url)
                .flatMap {
                    try {
                        val mapped = dataMapper.mapArtistSearchResults(it)
                        Result.Success(mapped)
                    }
                    catch (ex: Exception){
                        Result.Failure(ex)
                    }
                }
        }
        catch (ex: Exception){
            Result.error(ex)
        }

    }

    suspend fun getArtistByName(name : String) : Result<Artist,Exception> = coroutineScope {

        val url = buildUrl("artist.getinfo", Pair("artist",name))
        return@coroutineScope try{
            getResultFromApi<ArtistInfoFromLastFm,ExtendedErrorFromLastFm>(url)
                .flatMap {
                    try {
                        val mapped = dataMapper.mapArtistInfo(it)
                        Result.success(mapped)
                    }
                    catch (ex: Exception){
                        Result.error(ex)
                    }
                }
        }
        catch(ex: Exception){
            Result.error(ex)
        }
    }

    suspend fun getTopAlbumsOfArtist(
        artistName: String,
        startPage: Int,
        limitPerPage : Int
    ) : Result<TopAlbumOfArtistResult,Exception> = coroutineScope {
        val url = buildUrl(
            "artist.gettopalbums",
            Pair("artist",artistName),
            Pair("page","$startPage"),
            Pair("limit","$limitPerPage")
        )

        try{
            //The following transformation should map all TopAlbum-Informations to actual Albums
            //with all their information like Songs, Description, etc.
            return@coroutineScope getResultFromApi<TopAlbumsFromLastFm,ExtendedErrorFromLastFm>(url)
                .flatMap { topAlbumFromLastFm ->

                    val allSuccessfulLoadedAlbumsDeferred = async {
                        getAllValidTopAlbums(topAlbumFromLastFm)
                    }

                    val allSuccessFullLoadedAlbums = allSuccessfulLoadedAlbumsDeferred.await()

                    try {
                        val mapped = dataMapper.mapTopAlbumsResult(
                            topAlbumFromLastFm,allSuccessFullLoadedAlbums)
                        Result.Success(mapped)
                    }
                    catch (ex: Exception){
                       Result.error(ex)
                    }
                }

        }
        catch(ex: Exception){
            return@coroutineScope Result.error(ex)
        }
    }

    suspend fun getAlbum(
        artistName: String,
        albumTitle: String
    ) : Result<Album,Exception> = coroutineScope {
        val url = buildUrl(
            "album.getinfo",
            Pair("artist",artistName),
            Pair("album",albumTitle)
        )

        return@coroutineScope try {
            getResultFromApi<AlbumInfoFromLastFm,ExtendedErrorFromLastFm>(url).flatMap {
                try {
                    val mapped = dataMapper.mapAlbumInfo(it,artistName)
                    Result.Success(mapped)
                }
                catch (ex: Exception){
                    Result.Failure(ex)
                }
            }
        }
        catch (ex: Exception){
            Result.error(ex)
        }
    }

    //region generic help functions
    /**
     * This function can fetch a certain result from the API using a GET-Request.
     *
     * @param url The Url to fetch Data from via GET
     * @return A Success-Result with the specified ApiDataType or an Error-Result with ann Exception
     *
     * NOTE: Specifying the ApiErrorType is necessary due to the fact that LastFM-API might return
     * an Error-Object instead of handling an Error via a Status-Code. If such an Error-Object is
     * returned and matches the specified ApiErrorType, this Error-Object is automatically transferred
     * into an Error-Result encapsulating the associated Error-Message.
     * */
    private suspend inline fun <reified ApiDataType : Any, reified ApiErrorType : ErrorFromLastFm> getResultFromApi(
            url: String
    ): Result<ApiDataType, Exception> = coroutineScope{
        val (_, _, result) = Fuel
                .get(url)
                .header(Headers.USER_AGENT, BuildConfig.LastFMUserAgent)
                .responseString()

        val json = result.get()
        return@coroutineScope result.flatMap {

            if(jsonPropCheck.jsonObjectContainsAllPropertiesOf(ApiDataType::class, json)){
                val data = gson.fromJson<ApiDataType>(json, object : TypeToken<ApiDataType>() {}.type)
                if(data != null){
                    return@flatMap Result.success(data)
                }
            }

            if(jsonPropCheck.jsonObjectContainsAllPropertiesOf(ApiErrorType::class, json)){
                val err = gson.fromJson<ApiErrorType>(json, object : TypeToken<ApiErrorType>() {}.type)
                if(err != null){
                    return@flatMap Result.error(dataMapper.mapError(err))
                }
            }

            return@flatMap Result.error(NullPointerException("some unknown json was delivered by Api"))
        }
    }

    /**
     * This function builds the correct api call-url depending on the given parameters.
     * Additionally to that it automatically applies constant values to the URL that are always the
     * same such as format or API-Key.
     *
     * @param method The API-Method to be called
     * @param furtherParams Further Params that need to be added to the url
     *
     * @return The needed URL as a String
     * */
    private fun buildUrl(method: String, vararg furtherParams: Pair<String,String>) : String{
        val baseUrl = LAST_FM_API_BASE_URL +
                "?method=${method}" +
                "&api_key=${BuildConfig.LastFMApiKey}" +
                "&format=json"
        return buildString {
            append(baseUrl)
            furtherParams.forEach { param ->
                append("&${param.first}=${paramFormat.formatParamForUrl(param.second)}")
            }
        }
    }
    //endregion

    //region specific help functions
    /**
     * This function gets all valid TopAlbums of an Artist with all its information based on the
     * LastFM-Response for a certain amount of TopAlbums.
     *
     * @param mbidOfArtist The mbid of the Artist
     * @param topAlbumFromLastFm The Top-Albums response from LastFM
     *
     * @return all valid Top-Albums with all their information. Those information are acquired by
     * using the getAlbum-Function.
     * */
    private suspend fun getAllValidTopAlbums(
        topAlbumFromLastFm : TopAlbumsFromLastFm
    ) : List<Album> = coroutineScope {

        //This field will be a list of Pairs containing the ArtistName and the Album-Title
        val artistTitleTuples = topAlbumFromLastFm.topalbums.shortAlbumInfo.map {
            Pair(it.artist.name,it.name)
        }.filter {
            //Filter all Albums that are not null-Albums
            // (idk why those are returned by the API sometimes xD)
            !it.second.matches(Regex("\\(null\\)"))
        }

        /*after all "valid" topalbums are determined it is necessary to fetch the info
        for them from the Api using the provided function for fetching a single
        Album information*/
        val albumsDeferred = mutableListOf<Deferred<Result<Album,Exception>>>()

        artistTitleTuples.forEach { artistTitleData ->
            albumsDeferred.add(async {
                getAlbum(artistTitleData.first,artistTitleData.second)
            })
        }

        return@coroutineScope albumsDeferred.map {
            it.await()
        }.mapNotNull{
            when(it){
                is Result.Failure -> {
                    it.error.printStackTrace()
                    null
                }
                is Result.Success -> it.value
            }
        }
    }
    //endregion

}