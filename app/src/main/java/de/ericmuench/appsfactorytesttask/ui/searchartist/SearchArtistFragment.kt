package de.ericmuench.appsfactorytesttask.ui.searchartist

import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import de.ericmuench.appsfactorytesttask.R
import de.ericmuench.appsfactorytesttask.databinding.FragmentSearchArtistBinding
import de.ericmuench.appsfactorytesttask.ui.uicomponents.recyclerview.GenericSimpleItemAdapter
import de.ericmuench.appsfactorytesttask.util.extensions.*
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass, which is responsible for the search of an artist.
 */
class SearchArtistFragment : Fragment() {

    //fields
    private lateinit var viewBinding : FragmentSearchArtistBinding

    //TODO: remove dummy data
    val dummyData = List<String>(20){
        "Item ${it+1}"
    }.toMutableList()

    private var adapter : GenericSimpleItemAdapter<String>? = null

    //lifecycle functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        setHasOptionsMenu(true)
        viewBinding = FragmentSearchArtistBinding.inflate(inflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //setup layout
        setupRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_artist_actionbar_menu,menu)

        menu.findItem(R.id.search_artist_acbar_item_searchbar).actionView.castedAs<SearchView> {
            it.removeSearchPlate()
            it.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    hideKeyboard()
                    println("Submit Text: $query")
                    //TODO: add code for submitting the search text
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

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.acBarTest -> {
                adapter?.addElement("new Data ${adapter?.itemCount ?: 0 + 1}")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }*/


    //help functions for Layout setup
    //TODO: Documentation
    private fun setupRecyclerView() = with(viewBinding){
        //setHasFixedSize(true)
        activity.notNull { act ->
            recyclerviewSearchArtist.layoutManager = if(act.runsInLandscape()) GridLayoutManager(act,2) else LinearLayoutManager(act)
            adapter = GenericSimpleItemAdapter(act,dummyData)
                .onApplyDataToViewHolder { holder, str, pos ->
                    holder.txtText.text = str
                    holder.checkBox.setButtonDrawable(R.drawable.item_stored_selector)
                    holder.checkBox.setOnCheckedChangeListener { box, checked ->
                        println("value of $str is now $checked")
                    }

                    holder.cardView.setOnClickListener {
                        println("$str was clicked")
                    }
                }

            recyclerviewSearchArtist.adapter = adapter
        }
    }
}