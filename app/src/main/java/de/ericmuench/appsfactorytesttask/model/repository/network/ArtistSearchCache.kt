package de.ericmuench.appsfactorytesttask.model.repository.network

import de.ericmuench.appsfactorytesttask.model.runtime.ArtistSearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope


private typealias ArtistSearchResultChunks = List<ArtistSearchResult>
/**
 * This class caches the state of the search (is user currently searching and with which query)
 * as well as some search results.
 */
class ArtistSearchCache {

    //fields
    /**
     * This field determines in which time frames an entry should be refreshed. At the moment
     * its 60 Seconds. Changes in the caching time can be made here later.
     * */
    private val refreshTimeInterval = 60000L

    /**This field stores, whether the user is currently searching or not*/
    var isSearching = false
    /**This field stores the last submitted search query*/
    var lastSearchQuery = ""
    /**This field stores the last pending search-query*/
    var pendingQuery = ""

    private val searchResultsCache = mutableMapOf<String,Pair<ArtistSearchResultChunks,Long>>()

    //functions
    /**
     * This function can returns SearchResults from the internal data-structure. For that,
     * the following steps are done:
     * 1) If the data, associated with the current searchquery is not up-to-date it is deleted
     * 2) If there is no data or an invalid amount of data accociated with a certain page-value
     *    null is returned
     * 3) If the limit does not match the data is also deleted and null is returned
     *
     * @param searchQuery The searchQuery to associate the data with
     * @param startPage the startpage of the search-data
     * @param limitPerPage the page limit
     *
     * @return The cached data or null if there is no cached data
     *
     * NOTE: The whole data-entry for a certain search-query is deleted if the limitPerPage value
     * of results changes
     */
    fun getData(searchQuery : String, startPage : Int, limitPerPage : Int) : ArtistSearchResult?{
        removeIfOld(searchQuery)
        val timedChunks = searchResultsCache[searchQuery] ?: return null

        val result = timedChunks.first
                .filter {
                    it.startPage == startPage
                }
                .takeIf {
                    it.size == 1
                }
                ?.first() ?: return null
        if(result.itemsPerPage != limitPerPage){
            //remove entry because limit does not fit anymore
            searchResultsCache.remove(searchQuery)
            return null
        }

        return result
    }

    /**
     * This function can merge SearchResults into the internal data-structure for caching
     * search-results. For that, the following steps are done:
     * 1) If the data, associated with the current searchquery is not up-to-date it is deleted
     * 2) If there is no data for the searchquery, a new entry in the internal data-structure is
     *    made via addData-Function.
     * 3) If there is data existing, then this data will either be updated if the page-value is
     *    the same or added to the list of results if the page value does not already exist. This
     *    happens via the updateData-Function
     *
     * @param searchQuery The searchQuery to associate the data with
     * @param startPage the startpage of the search-data
     * @param limitPerPage the page limit
     * @param results The actual data to be merged
     *
     * @return Whether the merge-Operation was successful
     *
     * NOTE: The whole data-entry for a certain search-query is deleted if the limitPerPage value
     * of results changes
     */
    fun mergeData(
            searchQuery : String,
            startPage : Int,
            limitPerPage : Int,
            results: ArtistSearchResult
    ) : Boolean{
        removeIfOld(searchQuery)
        val timedChunks = searchResultsCache[searchQuery]
        return if(timedChunks == null){
            //add data
            addData(searchQuery,results)
        }
        else{
            //update data
            val chunks = timedChunks.first
            updateData(searchQuery,startPage,limitPerPage, results, chunks)
        }
    }

    /**
     * This function loads the last search result of th user
     * */
    fun lastSearchResult() : ArtistSearchResultChunks{
        val firstRes = searchResultsCache[lastSearchQuery]
                ?.first
                ?.takeIf {
                    it.isNotEmpty()
                }
                ?.first() ?: return emptyList()

        return listOf(firstRes)
    }

    /**
     * This function eliminates duplicates in an ArtistSearchResult acording to the already cached
     * data for a certain searchquery.
     *
     * @param searchQuery The Search-Query that should be used to check the data associated with it
     * @param searchResult The Search-Results to be filtered
     *
     * @return A filtered Version of searchResult that has no duplicates in its items according to
     * the already cached data
     * */
    suspend fun eliminateDuplicates(
            searchQuery: String,
            searchResult: ArtistSearchResult
    ) = coroutineScope{
        val allArtists = searchResultsCache[searchQuery]
                ?.first
                ?.map { it.items }
                ?.flatten() ?: emptyList()
        val distinctArtists = searchResult.items
                .filter { artist ->
                    !allArtists.contains(artist)
                }
        return@coroutineScope ArtistSearchResult(
                searchResult.totalResults,
                searchResult.startPage,
                searchResult.startIndex,
                distinctArtists.size,
                distinctArtists
        )

    }

    //help functions
    /**This function can add data to the internal data-structure*/
    private fun addData(searchQuery : String, results: ArtistSearchResult) : Boolean{
        searchResultsCache[searchQuery] = Pair(
                listOf(results),
                System.currentTimeMillis()
        )
        return true
    }

    /**This function can update certain data in the internal data-structure*/
    private fun updateData(
            searchQuery: String,
            startPage: Int,
            limitPerPage: Int,
            results: ArtistSearchResult,
            chunks: ArtistSearchResultChunks
    ) : Boolean{
        val modifyIndex = chunks
                .indexOfFirst {
                    it.startPage == startPage
                }

        return when {
            modifyIndex < 0 -> {
                searchResultsCache[searchQuery] = Pair(
                        chunks.toMutableList().apply {
                            add(results)
                        },
                        System.currentTimeMillis()
                )
                true
            }
            chunks[modifyIndex].itemsPerPage != limitPerPage -> {
                searchResultsCache.remove(searchQuery)
                addData(searchQuery,results)
            }
            else -> {
                searchResultsCache[searchQuery] = Pair(
                        chunks.toMutableList().apply {
                            set(modifyIndex,results)
                        },
                        System.currentTimeMillis()
                )
                true
            }
        }
    }

    /***
     * This function checks if an entry in the internal data-structure is up-to-date by checking its
     * timestamp.
     *
     * @param timedChunks an entry in the internal data-structure
     * @return whether the entry is up-to-date or not
     */
    private fun isUpToDate(timedChunks :Pair<ArtistSearchResultChunks,Long>) : Boolean{
        val currentTime = System.currentTimeMillis()
        return currentTime - timedChunks.second <= refreshTimeInterval
    }

    /**
     * This function checks if the value that is stored in the internal data-structure for a specific
     * searchquery is old. If yes, the data will be deleted.
     *
     * @param searchQuery The searchquery to check data of
     * @return whether the associated data was deleted or not
     * */
    private fun removeIfOld(searchQuery: String) : Boolean{
        val timedChunks = searchResultsCache[searchQuery] ?: return false

        if(!isUpToDate(timedChunks)){
            searchResultsCache.remove(searchQuery)
            return true
        }

        return false
    }
}