package de.ericmuench.appsfactorytesttask.ui.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import de.ericmuench.appsfactorytesttask.R
import de.ericmuench.appsfactorytesttask.app.AppsFactoryTestTaskApplication
import de.ericmuench.appsfactorytesttask.app.constants.INTENT_KEY_SEARCH_ARTIST_TO_ARTIST_DETAIL_TRANSFERRED_ARTIST
import de.ericmuench.appsfactorytesttask.app.constants.INTENT_KEY_TRANSFER_ALBUM
import de.ericmuench.appsfactorytesttask.app.constants.INTENT_KEY_TRANSFER_ALBUM_REFRESH_INDEX
import de.ericmuench.appsfactorytesttask.app.constants.REQUEST_ARTIST_DETAIL_ALBUM_STORE_STATE_REFRESH
import de.ericmuench.appsfactorytesttask.model.runtime.Album
import de.ericmuench.appsfactorytesttask.model.runtime.Artist
import de.ericmuench.appsfactorytesttask.ui.uicomponents.recyclerview.adapter.listadapter.GenericSimpleItemListAdapter
import de.ericmuench.appsfactorytesttask.ui.uicomponents.scrolling.NestedScrollViewPositionDetector
import de.ericmuench.appsfactorytesttask.util.extensions.notNull
import de.ericmuench.appsfactorytesttask.util.extensions.switchToActivityForResult
import de.ericmuench.appsfactorytesttask.util.loading.LoadingState
import de.ericmuench.appsfactorytesttask.viewmodel.ArtistDetailViewModel
import de.ericmuench.appsfactorytesttask.viewmodel.ArtistDetailViewModelFactory


class ArtistDetailActivity : DetailActivity() {

    //region fields
    private val viewModel : ArtistDetailViewModel by viewModels{
        val app = application as AppsFactoryTestTaskApplication
        ArtistDetailViewModelFactory(app.dataRepository)
    }


    private var recyclerViewAdapter : GenericSimpleItemListAdapter<Album>? = null
    private val scrollViewPositionDetector = NestedScrollViewPositionDetector().apply {
        onEndReached = {
            val hasInternet = internetConnectivityChecker
                .internetConnectivityState
                .hasInternetConnection
            viewModel.loadMoreAlbumData(hasInternet)
        }
    }
    //endregion

    //region Lifecycle functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //layout setup
        setupSpecificLayout()

        //vm setup
        setupViewModel()
    }

    override fun onStart() {
        super.onStart()

        //getting data from intent
        handleIntentData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_ARTIST_DETAIL_ALBUM_STORE_STATE_REFRESH){
            //refresh the index in the recyclerview that switched to AlbumDetail-Activity
            val refreshIdx = data?.getIntExtra(INTENT_KEY_TRANSFER_ALBUM_REFRESH_INDEX,-1) ?: -1
            val recyclerViewItemCount = recyclerViewAdapter?.itemCount ?: 0
            if(refreshIdx in 0 until recyclerViewItemCount){
                recyclerViewAdapter?.notifyItemChanged(refreshIdx)
            }
        }
    }
    //endregion

    //region Functions for setup Layout
    /**This function assigns specific values for Artist-Details to UI-Fields*/
    private fun setupSpecificLayout(){
        setupHeadlines()
        hideFabAction()
        hideAllProgressbars()
        hideLayerImage()

        //more btn
        setMoreButtonOnClickListener {
            viewModel.detailData.value?.onlineUrl.notNull { link -> openWebUrl(link) }
        }

        //reload button
        setOnReloadButtonClickListener {
            val hasInternet = internetConnectivityChecker
                .internetConnectivityState
                .hasInternetConnection

            viewModel.reloadData(hasInternet){ handleError(it) }
        }

        setNestedScrollViewOnScrollStateChangeListener(scrollViewPositionDetector)
    }

    override fun setupRecyclerView() {
        super.setupRecyclerView()
        //recyclerView-Adapter
        recyclerViewAdapter = GenericSimpleItemListAdapter<Album>(
            this,
            object: DiffUtil.ItemCallback<Album>(){
                override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
                    return oldItem == newItem
                }
            }
        )

        recyclerViewAdapter?.setOnApplyDataToViewHolder { holder, album, idx ->
            val drawableStore = ResourcesCompat.getDrawable(resources,R.drawable.ic_save,null)
            val drawableUnstore = ResourcesCompat.getDrawable(resources,R.drawable.ic_remove_circle,null)

            holder.imageButton.isEnabled = false
            viewModel.checkAlbumExistence(album, { handleError(it) }){ isStored ->
                val draw = if(isStored) drawableUnstore else drawableStore
                holder.imageButton.setImageDrawable(draw)
                holder.imageButton.isEnabled = true
            }

            holder.imageButton.setOnClickListener {
                holder.imageButton.isEnabled = false
                viewModel.switchStoreState(album, {
                    handleError(it)
                    holder.imageButton.isEnabled = true
                }){success ->
                    holder.imageButton.isEnabled = true
                    if (success){
                        notifyItemChanged(idx)
                    }

                }
            }

            holder.txtText.text = album.title
            holder.cardView.setOnClickListener {
                switchToActivityForResult<AlbumDetailActivity>(
                        REQUEST_ARTIST_DETAIL_ALBUM_STORE_STATE_REFRESH
                ){
                    putExtra(INTENT_KEY_TRANSFER_ALBUM,album)
                    putExtra(INTENT_KEY_TRANSFER_ALBUM_REFRESH_INDEX,idx)
                }
            }
        }
        recyclerView.adapter = recyclerViewAdapter
    }


    /**This function sets up the headlines. They are constant for a subclass of DetailActivity*/
    private fun setupHeadlines(){
        setDescriptionHeadline(resources.getString(R.string.about_artist))
        setDataHeadline(resources.getString(R.string.albums))
    }
    //endregion

    //region Setup-Functions for ViewModel
    private fun setupViewModel(){
        viewModel.detailData.observe(this){ artistData ->
            artistData.notNull { artist ->
                title = artist.artistName
                val description = artist
                    .description
                    .takeIf { it.isNotBlank() } ?: resources.getString(R.string.no_description_available)
                setDescription(description)
            }

            val onlineUrlAvailable = viewModel.detailData.value?.onlineUrl != null
            setMoreButtonIsEnabled(onlineUrlAvailable)
        }

        viewModel.topAlbumResults.observe(this){
            recyclerViewAdapter.notNull { recAdapter ->
                val allTopAlbums = viewModel.allTopAlbums
                recAdapter.submitList(allTopAlbums)
            }
        }

        viewModel.detailLoadingState.observe(this){ detailLoadingStateData ->
            detailLoadingStateData.notNull {
                setDescriptionLoading(it.isLoading)
            }
        }

        viewModel.albumsLoadingState.observe(this){ albumsLoadingStateData ->
            albumsLoadingStateData.notNull {
                if(!albumsLoadingStateData.isLoading){
                    hideAllDataProgressBars()
                    return@notNull
                }

                if (it == LoadingState.LOADING_MORE){
                    hideDataProgressBar()
                    showDataProgressBarBottom()
                }
                else{
                    hideDataProgressBarBottom()
                    showDataProgressBar()
                }
            }
        }

    }
    //endregion

    // region help functions
    /**
     * This function is responsible for getting transferred data out of the intent of this Activity.
     * If the data is valid and there is no data in the ViewModel already, the data will initially
     * be applied. If there is already data in the ViewModel, no data will be applied. If there is
     * no valid data, the Activity will finish.
     * */
    private fun handleIntentData(){
        val intentData = intent?.getParcelableExtra<Artist>(
            INTENT_KEY_SEARCH_ARTIST_TO_ARTIST_DETAIL_TRANSFERRED_ARTIST
        )

        if(intentData != null){
            if(viewModel.detailData.value == null){
                viewModel.initializeWithTransferredData(intentData)
                val hasInternet = internetConnectivityChecker
                    .internetConnectivityState
                    .hasInternetConnection

                viewModel.loadDataInitially(hasInternet){ handleError(it) }
            }
        }
        else{
            finish()
        }
    }
    //endregion
}