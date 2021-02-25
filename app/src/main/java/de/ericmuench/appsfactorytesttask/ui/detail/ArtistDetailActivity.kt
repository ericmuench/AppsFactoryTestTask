package de.ericmuench.appsfactorytesttask.ui.detail

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import de.ericmuench.appsfactorytesttask.R
import de.ericmuench.appsfactorytesttask.app.constants.INTENT_KEY_SEARCH_ARTIST_TO_ARTIST_DETAIL_TRANSFERRED_ARTIST
import de.ericmuench.appsfactorytesttask.app.constants.INTENT_KEY_TRANSFER_ALBUM
import de.ericmuench.appsfactorytesttask.model.runtime.Album
import de.ericmuench.appsfactorytesttask.model.runtime.Artist
import de.ericmuench.appsfactorytesttask.ui.uicomponents.recyclerview.GenericSimpleItemAdapter
import de.ericmuench.appsfactorytesttask.ui.uicomponents.scrolling.NestedScrollViewPositionDetector
import de.ericmuench.appsfactorytesttask.util.extensions.notNull
import de.ericmuench.appsfactorytesttask.util.extensions.switchToActivity
import de.ericmuench.appsfactorytesttask.util.loading.LoadingState
import de.ericmuench.appsfactorytesttask.viewmodel.ArtistDetailViewModel


class ArtistDetailActivity : DetailActivity() {

    //region fields
    private val viewModel : ArtistDetailViewModel by viewModels()
    private var recyclerViewAdapter : GenericSimpleItemAdapter<Album>? = null
    private val scrollViewPositionDetector = NestedScrollViewPositionDetector().apply {
        onEndReached = {
            val hasInternet = internetConnectivityChecker
                .internetConnectivityState
                .hasInternetConnection
            viewModel.loadMoreAlbumData(hasInternet)
        }
    }
    //endregion

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
        recyclerViewAdapter = GenericSimpleItemAdapter<Album>(this,viewModel.allTopAlbums)
            .onApplyDataToViewHolder { holder, album, idx ->
                val drawableStore = ResourcesCompat.getDrawable(resources,R.drawable.ic_save,null)
                val drawableUnStore = ResourcesCompat.getDrawable(resources,R.drawable.ic_remove_circle,null)
                holder.imageButton.setImageDrawable(drawableStore)
                holder.imageButton.setOnClickListener {
                    //TODO: Change action
                    if(holder.imageButton.drawable == drawableStore){
                        holder.imageButton.setImageDrawable(drawableUnStore)
                        return@setOnClickListener
                    }

                    holder.imageButton.setImageDrawable(drawableStore)
                }



                holder.txtText.text = album.title
                holder.cardView.setOnClickListener {
                    switchToActivity<AlbumDetailActivity>(){
                        putExtra(INTENT_KEY_TRANSFER_ALBUM,album)
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
                //TODO: Handle Image with glide
            }

            val onlineUrlAvailable = viewModel.detailData.value?.onlineUrl != null
            setMoreButtonIsEnabled(onlineUrlAvailable)
        }

        viewModel.topAlbumResults.observe(this){
            recyclerViewAdapter.notNull { recAdapter ->
                val allTopAlbums = viewModel.allTopAlbums
                if(recAdapter.itemCount < allTopAlbums.size){
                    //add data
                    recAdapter.addElements(allTopAlbums.subList(recAdapter.itemCount,allTopAlbums.size))
                }
                else if(recAdapter.itemCount > allTopAlbums.size){
                    //TODO: Remove/Reassign data
                }
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

    //region Further functions
    //endregion
}