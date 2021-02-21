package de.ericmuench.appsfactorytesttask.clerk.network

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.flatMap
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.ericmuench.appsfactorytesttask.BuildConfig
import de.ericmuench.appsfactorytesttask.clerk.mapper.ApiModelToRuntimeMapper
import de.ericmuench.appsfactorytesttask.model.lastfm.albuminfo.AlbumInfoFromLastFm
import de.ericmuench.appsfactorytesttask.model.lastfm.artistinfo.ArtistInfoFromLastFm
import de.ericmuench.appsfactorytesttask.model.lastfm.artistsearch.ArtistSearchResultFromLastFm
import de.ericmuench.appsfactorytesttask.model.lastfm.error.ErrorFromLastFm
import de.ericmuench.appsfactorytesttask.model.lastfm.error.ExtendedErrorFromLastFm
import de.ericmuench.appsfactorytesttask.model.runtime.Album
import de.ericmuench.appsfactorytesttask.model.runtime.Artist
import de.ericmuench.appsfactorytesttask.model.runtime.ArtistSearchResult
import de.ericmuench.appsfactorytesttask.util.formatter.UrlParameterFormatter
import de.ericmuench.appsfactorytesttask.util.json.GsonPropertyChecker
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
        val url = LAST_FM_API_BASE_URL +
                "?method=artist.search" +
                "&api_key=${BuildConfig.LastFMApiKey}" +
                "&artist=${paramFormat.formatParamForUrl(searchQuery)}" +
                "&page=$startPage" +
                "&limit=$elementsPerPage" +
                "&format=json"

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

    suspend fun getArtist(mbid : String) : Result<Artist,Exception> = coroutineScope {
        val url = LAST_FM_API_BASE_URL +
                "?method=artist.getinfo" +
                "&api_key=${BuildConfig.LastFMApiKey}" +
                "&mbid=${mbid}" +
                "&format=json"

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

    suspend fun getTopAlbumsOfArtist(mbidOfArtist: String) : Result<List<Album>,Exception> = coroutineScope {
        val url = LAST_FM_API_BASE_URL +
                "?method=artist.gettopalbums" +
                "&api_key=${BuildConfig.LastFMApiKey}" +
                "&mbid=${mbidOfArtist}" +
                "&format=json"


        //TODO: remove this
        return@coroutineScope Result.error(Exception("test"))
    }

    suspend fun getAlbum(artist: String, albumTitle: String) : Result<Album,Exception> = coroutineScope {
        val url = LAST_FM_API_BASE_URL +
                "?method=album.getinfo" +
                "&api_key=${BuildConfig.LastFMApiKey}" +
                "&artist=${paramFormat.formatParamForUrl(artist)}" +
                "&album=${paramFormat.formatParamForUrl(albumTitle)}" +
                "&format=json"

        return@coroutineScope try {
            getResultFromApi<AlbumInfoFromLastFm,ExtendedErrorFromLastFm>(url).flatMap {
                try {
                    val mapped = dataMapper.mapAlbumInfo(it)
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

    //help functions
    //generic data fetch function
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

}