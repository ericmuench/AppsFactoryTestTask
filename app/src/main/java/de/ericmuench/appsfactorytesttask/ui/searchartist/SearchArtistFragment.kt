package de.ericmuench.appsfactorytesttask.ui.searchartist

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import de.ericmuench.appsfactorytesttask.R
import de.ericmuench.appsfactorytesttask.app.AppsFactoryTestTaskApplication
import de.ericmuench.appsfactorytesttask.app.constants.INTENT_KEY_SEARCH_ARTIST_TO_ARTIST_DETAIL_TRANSFERRED_ARTIST
import de.ericmuench.appsfactorytesttask.databinding.FragmentSearchArtistBinding
import de.ericmuench.appsfactorytesttask.model.runtime.Artist
import de.ericmuench.appsfactorytesttask.ui.detail.ArtistDetailActivity
import de.ericmuench.appsfactorytesttask.ui.uicomponents.abstract_activities_fragments.BaseActivity
import de.ericmuench.appsfactorytesttask.ui.uicomponents.abstract_activities_fragments.BaseFragment
import de.ericmuench.appsfactorytesttask.ui.uicomponents.recyclerview.adapter.GenericSimpleItemAdapter
import de.ericmuench.appsfactorytesttask.ui.uicomponents.recyclerview.RecyclerViewScrollPositionDetector
import de.ericmuench.appsfactorytesttask.util.extensions.*
import de.ericmuench.appsfactorytesttask.util.loading.LoadingState
import de.ericmuench.appsfactorytesttask.viewmodel.SearchArtistViewModel
import de.ericmuench.appsfactorytesttask.viewmodel.SearchArtistViewModelFactory
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass, which is responsible for the search of an artist.
 */
class SearchArtistFragment : BaseFragment() {

    //region fields
    private lateinit var viewBinding : FragmentSearchArtistBinding
    private val viewModel : SearchArtistViewModel by viewModels{
        val application = requireActivity().application as AppsFactoryTestTaskApplication
        SearchArtistViewModelFactory(application.dataRepository)
    }

    private var recyclerViewAdapter : GenericSimpleItemAdapter<Artist>? = null
    private val recyclerViewPositionDetector = RecyclerViewScrollPositionDetector().apply {
        onEndReached = {
            activity?.castedAs<BaseActivity> {
                val hasInternet = it
                    .internetConnectivityChecker
                    .internetConnectivityState
                    .hasInternetConnection

                viewModel.loadMoreSearchData(hasInternet)
            }
        }
    }

    private var searchViewItem : SearchView? = null
    //endregion

    //region lifecycle functions
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View{
        setHasOptionsMenu(true)
        viewBinding = FragmentSearchArtistBinding.inflate(inflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViewBasics()
        setupViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_artist_actionbar_menu,menu)

        //SearchView
        val menuItem = menu.findItem(R.id.search_artist_acbar_item_searchbar)
        setupSearchView(menuItem)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.search_artist_acbar_item_clear_items -> {
                requestSearchResultsClear()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()
        viewBinding.recyclerviewSearchArtist.addOnScrollListener(recyclerViewPositionDetector)
    }

    override fun onStop() {
        super.onStop()
        viewBinding.recyclerviewSearchArtist.clearOnScrollListeners()
    }
    //endregion

    //region help functions for Layout setup
    /**
     * This function sets up all basic components that are related to the recyclerview of this Fragment
     * displaying the Search-Result for artists. The only aspect that is necessary to be set up later
     * is the scroll listener given that it needs to be removed in onStop.
     */
    private fun setupRecyclerViewBasics() = with(viewBinding){
        activity.notNull { act ->
            recyclerviewSearchArtist.layoutManager = if(act.runsInLandscape()) {
                GridLayoutManager(act,2)
            }
            else {
                LinearLayoutManager(act)
            }

            recyclerViewAdapter = GenericSimpleItemAdapter(act, emptyList<Artist>()).apply {
                setOnApplyDataToViewHolder { holder, artist, _ ->
                    holder.txtText.text = artist.artistName
                    holder.imageButton.visibility = View.INVISIBLE
                    holder.cardView.setOnClickListener {
                        switchToActivity<ArtistDetailActivity>(){
                            putExtra(
                                INTENT_KEY_SEARCH_ARTIST_TO_ARTIST_DETAIL_TRANSFERRED_ARTIST,
                                artist
                            )
                        }
                    }
                }
            }
            recyclerviewSearchArtist.adapter = recyclerViewAdapter
        }
    }

    /**This function sets up all UI Components associated with the SearchView*/
    private fun setupSearchView(menuItem : MenuItem?){

        val isSearching = viewModel.isSearchingArtist
        if(isSearching){
            menuItem?.expandActionView()
        }

        menuItem?.actionView.castedAs<SearchView> { searchView ->
            searchViewItem = searchView
            searchView.removeSearchPlate()

            searchView.setQuery(viewModel.pendingArtistSearchQuery,false)
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query.notNull { queryNN ->
                        lifecycleScope.launch {
                            hideKeyboard()
                            viewModel.artistSearchQuery = queryNN

                            activity?.castedAs<BaseActivity> { baseActivity ->

                                val hasInternet = baseActivity.internetConnectivityChecker
                                    .internetConnectivityState
                                    .hasInternetConnection
                                viewModel.submitArtistSearchQuery(hasInternet){
                                    handleError(it)
                                }
                            }

                        }
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if(!searchView.isIconified && isVisible){
                        viewModel.pendingArtistSearchQuery = newText ?: ""
                    }
                    return true
                }
            })
        }


        menuItem?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                viewModel.isSearchingArtist = true
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                viewModel.isSearchingArtist = false
                return true
            }
        })
    }
    //endregion

    //region help functions for Viewmodel setup
    private fun setupViewModel() = with(viewModel){
        //observer for search results
        searchedArtistsResultChunks.observe(viewLifecycleOwner) { vmSearchData ->
            lifecycleScope.launch {
                val recAdapter = recyclerViewAdapter
                if(vmSearchData != null && recAdapter != null){
                    val allArtists = viewModel.allArtists

                    when {
                        allArtists.isEmpty() -> {
                            recAdapter.clearElements()
                        }
                        recAdapter.itemCount < allArtists.size -> {
                            allArtists.forEach {
                                println("Artist: ${it.artistName}")
                            }
                            recAdapter.addElements(allArtists.subList(recAdapter.itemCount,allArtists.size))
                        }
                        recAdapter.itemCount > allArtists.size -> {
                            recAdapter.clearElements()
                            recAdapter.addElements(allArtists)
                        }
                    }
                }
            }
        }

        //observe loadingstate
        loadingState.observe(viewLifecycleOwner){ vmLoadState ->
            vmLoadState.notNull { loading ->
                when(loading){
                   LoadingState.IDLE -> {
                       viewBinding.progressTopSearchArtist.root.hide()
                       viewBinding.progressBottomSearchArtist.root.hide()
                   }
                   LoadingState.LOADING -> {
                       viewBinding.progressTopSearchArtist.root.show()
                       viewBinding.progressBottomSearchArtist.root.hide()
                   }
                   LoadingState.RELOADING -> {
                       viewBinding.progressTopSearchArtist.root.show()
                       viewBinding.progressBottomSearchArtist.root.hide()
                   }
                   LoadingState.LOADING_MORE -> {
                       viewBinding.progressTopSearchArtist.root.hide()
                       viewBinding.progressBottomSearchArtist.root.show()
                   }
                }
            }
        }

        //load init data
        viewModel.loadLastSearchResults()
    }
    //endregion

    //region further help functions
    /**
     * This function should ask the user if he/she wants to delete the results of the search and
     * do so if that's the case.
     * */
    private fun requestSearchResultsClear(){
        val con = context
        if(viewModel.hasSearchResults() && con != null){
            AlertDialog.Builder(con)
                    .setTitle(R.string.warning)
                    .setPositiveButton(android.R.string.yes){ dialog, _ ->
                        searchViewItem?.setQuery("",false)
                        viewModel.artistSearchQuery = ""
                        viewModel.clearArtistSearchData()
                        dialog.dismiss()
                    }
                    .setNegativeButton(android.R.string.no){ dialog, _ ->
                        dialog.dismiss()
                    }
                    .setMessage(R.string.question_sure_clear_all_artist_search_results)
                    .create()
                    .show()
        }
    }
    //endregion
}