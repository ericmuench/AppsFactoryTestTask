package de.ericmuench.appsfactorytesttask.ui.detail

import android.os.Bundle
import androidx.activity.viewModels
import de.ericmuench.appsfactorytesttask.R
import de.ericmuench.appsfactorytesttask.app.constants.INTENT_KEY_SEARCH_ARTIST_TO_ARTIST_DETAIL_TRANSFERRED_ARTIST
import de.ericmuench.appsfactorytesttask.model.runtime.Album
import de.ericmuench.appsfactorytesttask.model.runtime.Artist
import de.ericmuench.appsfactorytesttask.ui.uicomponents.recyclerview.GenericSimpleItemAdapter
import de.ericmuench.appsfactorytesttask.ui.uicomponents.recyclerview.RecyclerViewScrollPositionDetector
import de.ericmuench.appsfactorytesttask.ui.uicomponents.scrolling.NestedScrollViewPositionDetector
import de.ericmuench.appsfactorytesttask.util.connectivity.ConnectivityChecker
import de.ericmuench.appsfactorytesttask.util.extensions.notNull
import de.ericmuench.appsfactorytesttask.viewmodel.ArtistDetailViewModel


class ArtistDetailActivity : DetailActivity() {

    //region fields
    private val viewModel : ArtistDetailViewModel by viewModels()
    private var recyclerViewAdapter : GenericSimpleItemAdapter<Album>? = null
    private val scrollViewPositionDetector = NestedScrollViewPositionDetector().apply {
        onEndReached = {
            viewModel.loadMoreAlbumData(ConnectivityChecker()){ handleError(it)}
        }
    }
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //layout setup
        setupSpecificLayout()

        //vm setup
        setupViewModel()

        //getting data from intent
        handleIntentData()
    }

    //region Functions for setup Layout
    /**This function assigns specific values for Artist-Details to UI-Fields*/
    private fun setupSpecificLayout(){
        setupHeadlines()
        hideFabAction()

        //more btn
        setMoreButtonOnClickListener {
            viewModel.detailData.value?.onlineUrl.notNull { link -> openWebUrl(link) }
        }

        setNestedScrollViewOnScrollStateChangeListener(scrollViewPositionDetector)
    }

    override fun setupRecyclerView() {
        super.setupRecyclerView()
        //recyclerView-Adapter
        recyclerViewAdapter = GenericSimpleItemAdapter<Album>(this,viewModel.allTopAlbums)
            .onApplyDataToViewHolder { holder, album, idx ->
                //TODO: Maybe switch checkbox to image button later
                holder.checkBox.setButtonDrawable(R.drawable.item_stored_selector)
                holder.txtText.text = album.title
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
                    .takeIf { it.isNotBlank() } ?: resources.getString(R.string.no_album_description)
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
                viewModel.loadData{ handleError(it) }
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