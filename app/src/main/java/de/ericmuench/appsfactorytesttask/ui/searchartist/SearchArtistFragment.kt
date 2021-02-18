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
import de.ericmuench.appsfactorytesttask.databinding.FragmentSearchArtistBinding
import de.ericmuench.appsfactorytesttask.model.runtime.Artist
import de.ericmuench.appsfactorytesttask.ui.uicomponents.recyclerview.GenericSimpleItemAdapter
import de.ericmuench.appsfactorytesttask.ui.uicomponents.recyclerview.RecyclerViewPositionDetector
import de.ericmuench.appsfactorytesttask.util.extensions.*
import de.ericmuench.appsfactorytesttask.util.loading.LoadingState
import de.ericmuench.appsfactorytesttask.viewmodel.SearchArtistViewModel
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass, which is responsible for the search of an artist.
 */
class SearchArtistFragment : Fragment() {

    //fields
    private lateinit var viewBinding : FragmentSearchArtistBinding
    private val viewModel : SearchArtistViewModel by viewModels()

    private var recyclerViewAdapter : GenericSimpleItemAdapter<Artist>? = null
    private var searchViewItem : SearchView? = null

    //lifecycle functions
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View{
        setHasOptionsMenu(true)
        viewBinding = FragmentSearchArtistBinding.inflate(inflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_artist_actionbar_menu,menu)

        //SearchView
        val menuItem = menu.findItem(R.id.search_artist_acbar_item_searchbar)
        setupSearchView(menuItem)

    }

    override fun onStop() {
        super.onStop()
        viewBinding.recyclerviewSearchArtist.clearOnScrollListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        /*clear text change listener to remove SearchView-Bug that Text is not restored after
        * after rotation change
        * */
        searchViewItem?.setOnQueryTextFocusChangeListener(null)

    }

    //help functions for Layout setup
    /**
     * This function sets up all components that are related to the recyclerview of this Fragment
     * displaying the Search-Result for artists.
     */
    private fun setupRecyclerView() = with(viewBinding){
        activity.notNull { act ->
            recyclerviewSearchArtist.layoutManager = if(act.runsInLandscape()) GridLayoutManager(act,2) else LinearLayoutManager(act)
            recyclerViewAdapter = GenericSimpleItemAdapter(act, emptyList<Artist>())
                .onApplyDataToViewHolder { holder, artist, _ ->
                    holder.txtText.text = artist.artistName
                    /*holder.checkBox.setButtonDrawable(R.drawable.item_stored_selector)
                    holder.checkBox.setOnCheckedChangeListener { box, checked ->
                        println("value of $str is now $checked")
                    }*/
                    holder.checkBox.visibility = View.INVISIBLE
                    holder.cardView.setOnClickListener {
                        println("${artist.artistName} was clicked")
                        //TODO: Open detail page for artist (top album overview)
                    }
                }
            recyclerviewSearchArtist.adapter = recyclerViewAdapter

            val positionDetector = RecyclerViewPositionDetector().apply {
                onEndReached = {
                    viewModel.loadMoreSearchData { onHandleError(it) }
                }
            }
            recyclerviewSearchArtist.addOnScrollListener(positionDetector)
        }
    }

    //TODO: Doc
    private fun setupSearchView(menuItem : MenuItem?){

        val isSearching = viewModel.isSearching.value ?: false
        if(isSearching){
            menuItem?.expandActionView()
        }

        menuItem?.actionView.castedAs<SearchView> { searchView ->
            searchViewItem = searchView
            searchView.removeSearchPlate()
            searchView.setQuery(viewModel.searchQuery.value ?: "",false)
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query.notNull { queryNN ->
                        hideKeyboard()
                        println("Submit Text: $queryNN")
                        viewModel.searchQuery.value = queryNN
                        viewModel.submitArtistSearchQuery { onHandleError(it) }
                    }
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.searchQuery.value = newText
                    return false
                }
            })
        }


        menuItem?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                viewModel.isSearching.value = true
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                viewModel.isSearching.value = false
                return true
            }
        })
    }

    //help functions for Viewmodel setup
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
                       viewBinding.progressTopSearchArtist.hide()
                       viewBinding.progressBottomSearchArtist.hide()
                   }
                   LoadingState.LOADING -> {
                       viewBinding.progressTopSearchArtist.show()
                       viewBinding.progressBottomSearchArtist.hide()
                   }
                   LoadingState.RELOADING -> {
                       viewBinding.progressTopSearchArtist.hide()
                       viewBinding.progressBottomSearchArtist.show()
                   }
                }
            }
        }
    }

    //help functions for error handling
    /**
     * This function handles an Error by showing Feedback in the UI for the User
     *
     * @param error A Throwable-Object which usually contains an Exception
     * */
     private fun onHandleError(error: Throwable){
        activity.notNull { act ->
            val errorMsg = resources
                    .getString(R.string.error_template_try_again)
                    .replace("#","\n\n${error.localizedMessage}\n\n")

            AlertDialog.Builder(act)
                    .setTitle(R.string.error)
                    .setMessage(errorMsg)
                    .setPositiveButton(android.R.string.ok) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    .create()
                    .show()
        }
        error.printStackTrace()
    }
}