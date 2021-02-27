package de.ericmuench.appsfactorytesttask.ui.storedalbums

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import de.ericmuench.appsfactorytesttask.R
import de.ericmuench.appsfactorytesttask.databinding.FragmentAlbumsOverviewBinding
import de.ericmuench.appsfactorytesttask.ui.uicomponents.abstract_activities_fragments.BaseFragment
import de.ericmuench.appsfactorytesttask.ui.uicomponents.recyclerview.GenericImagedItemAdapter
import de.ericmuench.appsfactorytesttask.util.extensions.runsInLandscape

/**
 * A simple [Fragment] subclass, that is responsible for displaying the locally stored albums.
 */
class StoredAlbumsFragment : BaseFragment() {

    //region Fields
    private lateinit var viewBinding : FragmentAlbumsOverviewBinding
    private var recyclerViewAdapter : GenericImagedItemAdapter<Int>? = null
    //endregion

    //region livecycle functions
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewBinding = FragmentAlbumsOverviewBinding.inflate(layoutInflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

    }
    //endregion

    //region Functions for Layout Setup
    private fun setupRecyclerView() = with(viewBinding){
        //layout manager
        val runsInLandscape = activity?.runsInLandscape() ?: false
        recyclerviewAlbumsOverview.layoutManager = if(runsInLandscape){
            GridLayoutManager(context,2)
        }
        else{
            LinearLayoutManager(context)
        }

        //adapter
        //TODO: Replace dummy
        val context = context ?: return@with
        val dummy = List(20){it+1}
        recyclerViewAdapter = GenericImagedItemAdapter(context,dummy)
        recyclerViewAdapter?.setOnApplyDataToViewHolder { holder, element, idx ->
            //TODO: Change to real apply code
            holder.cardView.setOnClickListener {
                Toast.makeText(context,"$element clicked",Toast.LENGTH_SHORT).show()
            }

            val draw = ResourcesCompat.getDrawable(resources,R.drawable.ic_person_profile_pic,null)
            holder.imageView.setImageDrawable(draw)

            holder.txtTitle.text = "Title #$element"
            holder.txtSubTitle.text = "Subtitle #$element"
        }

        recyclerviewAlbumsOverview.adapter = recyclerViewAdapter
    }
    //endregion
}