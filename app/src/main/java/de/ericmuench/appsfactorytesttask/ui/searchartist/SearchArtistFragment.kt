package de.ericmuench.appsfactorytesttask.ui.searchartist

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import de.ericmuench.appsfactorytesttask.R
import de.ericmuench.appsfactorytesttask.databinding.FragmentSearchArtistBinding
import de.ericmuench.appsfactorytesttask.model.runtime.Artist
import de.ericmuench.appsfactorytesttask.ui.uicomponents.recyclerview.GenericSimpleItemAdapter
import de.ericmuench.appsfactorytesttask.util.extensions.*
import de.ericmuench.appsfactorytesttask.viewmodel.SearchArtistViewModel

/**
 * A simple [Fragment] subclass, which is responsible for the search of an artist.
 */
class SearchArtistFragment : Fragment() {

    //fields
    private lateinit var viewBinding : FragmentSearchArtistBinding
    private val viewModel : SearchArtistViewModel by viewModels()

    private var recyclerViewAdapter : GenericSimpleItemAdapter<Artist>? = null

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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_artist_actionbar_menu,menu)

        menu.findItem(R.id.search_artist_acbar_item_searchbar).actionView.castedAs<SearchView> { searchView ->
            searchView.removeSearchPlate()
            searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query.notNull {
                        hideKeyboard()
                        println("Submit Text: $it")
                        viewModel.submitArtistSearchQuery(it)
                    }
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    println("Text changed : $newText")
                    //TODO: Sync search Text with VM
                    return false
                }
            })

        }
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
                .onApplyDataToViewHolder { holder, artist, pos ->
                    holder.txtText.text = artist.artistName
                    /*holder.checkBox.setButtonDrawable(R.drawable.item_stored_selector)
                    holder.checkBox.setOnCheckedChangeListener { box, checked ->
                        println("value of $str is now $checked")
                    }*/

                    holder.cardView.setOnClickListener {
                        println("${artist.artistName} was clicked")
                        //TODO: Open detail page for artist (top album overview)
                    }
                }
            recyclerviewSearchArtist.adapter = recyclerViewAdapter
        }
    }
}