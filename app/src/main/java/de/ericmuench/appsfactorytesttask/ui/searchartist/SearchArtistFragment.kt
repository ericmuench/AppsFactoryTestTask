package de.ericmuench.appsfactorytesttask.ui.searchartist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import de.ericmuench.appsfactorytesttask.R
import de.ericmuench.appsfactorytesttask.databinding.FragmentSearchArtistBinding
import de.ericmuench.appsfactorytesttask.ui.uicomponents.recyclerview.GenericSimpleItemAdapter
import de.ericmuench.appsfactorytesttask.util.extensions.*

/**
 * A simple [Fragment] subclass, which is responsible for the search of an artist.
 */
class SearchArtistFragment : Fragment() {

    //fields
    private lateinit var viewBinding : FragmentSearchArtistBinding

    //lifecycle functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        viewBinding = FragmentSearchArtistBinding.inflate(inflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //setup layout
        setupSearchComponents()
        setupRecyclerView()
    }

    //help functions for Layout setup
    /**
     * This function does the UI-Setup of the SearchView and all its related components
     */
    private fun setupSearchComponents() = with(viewBinding){
        searchviewSearchArtist.removeSearchPlate()
        searchviewSearchArtist.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
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

        imgbtnSearchArtist.setOnClickListener {
            //TODO add code for submitting the search text
            println("Submit Text: ${searchviewSearchArtist.query}")
        }

    }

    private fun setupRecyclerView() = with(viewBinding.recyclerviewSearchArtist){
        //TODO: remove dummy data
        val dummyData = List<String>(20){
            "Item ${it+1}"
        }

        activity.notNull { act ->
            layoutManager = if(act.runsInLandscape()) GridLayoutManager(act,2) else LinearLayoutManager(act)
            adapter = GenericSimpleItemAdapter(act,dummyData).onApplyDataToViewHolder { holder, str ->
                holder.txtText.text = str
                holder.btnIcon.setIconResource(R.drawable.ic_album)
                //holder.materialButtonTogleGroup.visibility = View.INVISIBLE
                holder.materialButtonTogleGroup.addOnButtonCheckedListener { _, _, checked ->
                    println("value of $str is now $checked")
                }
                holder.cardView.setOnClickListener {
                    println("$str was clicked")
                }
            }

            viewBinding.imgbtnSearchArtist.setOnClickListener {
                //TODO remove this dummy code
                adapter.castedAs<GenericSimpleItemAdapter<String>> { ada ->
                    ada.addElements(List(5){"Another Item #${it + 1 }"})
                }
            }
        }
    }
}