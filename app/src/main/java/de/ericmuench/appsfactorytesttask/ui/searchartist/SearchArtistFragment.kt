package de.ericmuench.appsfactorytesttask.ui.searchartist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import de.ericmuench.appsfactorytesttask.databinding.FragmentSearchArtistBinding
import de.ericmuench.appsfactorytesttask.util.extensions.hideKeyboard
import de.ericmuench.appsfactorytesttask.util.extensions.removeSearchPlate

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
        setupSearchView()
    }

    //help functions for Layout setup
    private fun setupSearchView() = with(viewBinding.searchviewSearchArtist){
        removeSearchPlate()
        setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                hideKeyboard()
                println("Submit Text: $query")
                //TODO: add code for submitting the search text
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                println("Text changed : $query")
                //TODO: Sync search Text with VM
                return false
            }
        })
    }
}