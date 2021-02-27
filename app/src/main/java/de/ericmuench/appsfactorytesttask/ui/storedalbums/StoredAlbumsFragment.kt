package de.ericmuench.appsfactorytesttask.ui.storedalbums

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import de.ericmuench.appsfactorytesttask.R
import de.ericmuench.appsfactorytesttask.app.AppsFactoryTestTaskApplication
import de.ericmuench.appsfactorytesttask.databinding.FragmentAlbumsOverviewBinding
import de.ericmuench.appsfactorytesttask.model.room.StoredAlbumInfo
import de.ericmuench.appsfactorytesttask.ui.uicomponents.abstract_activities_fragments.BaseFragment
import de.ericmuench.appsfactorytesttask.ui.uicomponents.recyclerview.adapter.GenericImagedItemAdapter
import de.ericmuench.appsfactorytesttask.ui.uicomponents.recyclerview.adapter.listadapter.GenericImagedItemListAdapter
import de.ericmuench.appsfactorytesttask.util.extensions.notNull
import de.ericmuench.appsfactorytesttask.util.extensions.runsInLandscape
import de.ericmuench.appsfactorytesttask.viewmodel.StoredAlbumsViewModel
import de.ericmuench.appsfactorytesttask.viewmodel.StoredAlbumsViewModelFactory
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass, that is responsible for displaying the locally stored albums.
 */
class StoredAlbumsFragment : BaseFragment() {

    //region Fields
    private val viewModel : StoredAlbumsViewModel by viewModels {
        val application = requireActivity().application as AppsFactoryTestTaskApplication
        StoredAlbumsViewModelFactory(application.dataRepository)
    }

    private lateinit var viewBinding : FragmentAlbumsOverviewBinding
    private var recyclerViewAdapter : GenericImagedItemListAdapter<StoredAlbumInfo>? = null
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
        setupViewModel()

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
        val context = context ?: return@with
        recyclerViewAdapter = GenericImagedItemListAdapter<StoredAlbumInfo>(context,
            object :DiffUtil.ItemCallback<StoredAlbumInfo>(){
            override fun areItemsTheSame(
                oldItem: StoredAlbumInfo,
                newItem: StoredAlbumInfo
            ): Boolean {
                return oldItem.alid == newItem.alid
            }

            override fun areContentsTheSame(
                oldItem: StoredAlbumInfo,
                newItem: StoredAlbumInfo
            ): Boolean {
                return oldItem == newItem
            }
        })

        recyclerViewAdapter?.setOnApplyDataToViewHolder { holder, albumInfo, idx ->
            holder.cardView.setOnClickListener {
                //TODO: open new screen with album
                Toast.makeText(context,"$albumInfo clicked",Toast.LENGTH_SHORT).show()
            }

            albumInfo.imgUrl.notNull { imageUrl ->
                lifecycleScope.launch {
                    Glide.with(context)
                        .load(imageUrl)
                        .centerCrop()
                        .placeholder(R.drawable.ic_album_loading)
                        .error(R.drawable.ic_album_default_image)
                        .into(holder.imageView)
                }
            }

            holder.txtTitle.text = albumInfo.title
            holder.txtSubTitle.text = albumInfo.artistName
        }

        recyclerviewAlbumsOverview.adapter = recyclerViewAdapter
    }
    //endregion

    //region Functions for ViewModel-Setup
    private fun setupViewModel() = with(viewModel){
        allStoredAlbums.observe(viewLifecycleOwner){
            lifecycleScope.launch {
                val newData = it ?: emptyList()
                //TODO Change to more efficent imlementation later
                recyclerViewAdapter?.submitList(newData)
            }
        }
    }
    //endregion
}