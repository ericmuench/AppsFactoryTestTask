package de.ericmuench.appsfactorytesttask.ui.detail

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import de.ericmuench.appsfactorytesttask.R
import de.ericmuench.appsfactorytesttask.app.AppsFactoryTestTaskApplication
import de.ericmuench.appsfactorytesttask.app.constants.INTENT_KEY_TRANSFER_ALBUM
import de.ericmuench.appsfactorytesttask.app.constants.INTENT_KEY_TRANSFER_ALBUM_REFRESH_INDEX
import de.ericmuench.appsfactorytesttask.model.runtime.Album
import de.ericmuench.appsfactorytesttask.model.runtime.Song
import de.ericmuench.appsfactorytesttask.ui.uicomponents.recyclerview.adapter.listadapter.GenericSimpleItemListAdapter
import de.ericmuench.appsfactorytesttask.util.extensions.finishWithResultData
import de.ericmuench.appsfactorytesttask.util.extensions.notNull
import de.ericmuench.appsfactorytesttask.viewmodel.AlbumDetailViewModel
import de.ericmuench.appsfactorytesttask.viewmodel.AlbumsDetailViewModelFactory
import kotlinx.coroutines.launch

class AlbumDetailActivity : DetailActivity() {

    //region Fields
    private val viewModel : AlbumDetailViewModel by viewModels{
        val app = application as AppsFactoryTestTaskApplication
        AlbumsDetailViewModelFactory(app.dataRepository)
    }

    private var recyclerViewAdapter : GenericSimpleItemListAdapter<Song>? = null
    //endregion


    //region Lifecycle Functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setup ViewModel
        setupViewModel()

        //setup Layout
        setupLayout()

        //apply intent data
        applyAlbumFromIntentOrFinish()

        //do existence check
        if(viewModel.isAlbumStored.value == null){
            viewModel.detailData.value?.notNull { album ->
                viewModel.checkAlbumExistence(album){handleError(it)}
            }
        }

    }

    override fun onActionbarBackButtonPressed() {
        onGoBack()
    }

    override fun onBackPressed() {
        onGoBack()
    }

    //endregion

    //region Layout Setup Functions
    private fun setupLayout(){
        //img
        lifecycleScope.launch {
            val defaultAlbumDrawable = ResourcesCompat
                .getDrawable(resources,R.drawable.ic_album_default_image,null)
            imgViewDetail.setImageDrawable(defaultAlbumDrawable)
        }

        setupHeadlines()
        shouldDisplayOptionsMenu = false
        hideAllProgressbars()
        setupFabAction()

        //more btn
        setMoreButtonOnClickListener {
            viewModel.detailData.value?.onlineUrl.notNull {
                openWebUrl(it)
            }
        }
    }

    override fun setupRecyclerView() {
        super.setupRecyclerView()
        recyclerViewAdapter = GenericSimpleItemListAdapter<Song>(
            this,
            object : DiffUtil.ItemCallback<Song>(){
                override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
                    return oldItem == newItem
                }
            }
        )

        recyclerViewAdapter?.setOnApplyDataToViewHolder { holder, song, _ ->
            holder.cardView.setOnClickListener {
                song.onlineUrl.notNull {songUrl ->
                    openWebUrl(songUrl)
                }
            }
            holder.imageButton.visibility = View.GONE
            holder.txtText.text = song.title
        }

        recyclerView.adapter = recyclerViewAdapter
    }

    private fun setupHeadlines(){
        setDescriptionHeadline(resources.getString(R.string.about_album))
        setDataHeadline(resources.getString(R.string.songs))
    }

    private fun setupFabAction(){
        setFabActionOnClickListener {
            viewModel.switchStoreState{ handleError(it) }
        }
    }
    //endregion

    //region Functions for Data-Handling (Interaction with ViewModel)
    private fun setupViewModel(){
        viewModel.detailData.observe(this){ albumData ->
            albumData.notNull { album ->
                //Apply Album Details to UI
                title = album.title

                album.imgUrl.notNull {
                    lifecycleScope.launch{
                        Glide.with(this@AlbumDetailActivity)
                            .load(album.imgUrl)
                            .centerCrop()
                            .placeholder(R.drawable.ic_album_loading)
                            .error(R.drawable.ic_album_default_image)
                            .into(imgViewDetail)
                    }
                }


                val description = album
                    .description
                    .takeIf { it.isNotBlank() }
                    ?: resources.getString(R.string.no_description_available)
                setDescription(description)

                recyclerViewAdapter?.submitList(album.songs)
            }
        }

        viewModel.isAlbumStored.observe(this){ isStoredData ->
            if(isStoredData != null){
                val fabDrawableId = if(isStoredData) R.drawable.ic_remove_circle else R.drawable.ic_save
                val fabDrawable = ResourcesCompat.getDrawable(
                    resources,
                    fabDrawableId,
                    null
                )
                setFabActionIconDrawable(fabDrawable)
                setFabActionEnabled(true)
            }
            else{
                setFabActionEnabled(false)
            }

        }

        viewModel.isProcessing.observe(this){ loadingStateData ->
            loadingStateData.notNull {
                setFabActionEnabled(!loadingStateData.isLoading)
            }
        }
    }
    //endregion

    //region Intent Handling
    private fun applyAlbumFromIntentOrFinish(){
        val albumFromIntent = intent?.getParcelableExtra<Album>(INTENT_KEY_TRANSFER_ALBUM)

        if(albumFromIntent != null){
            viewModel.initializeWithTransferredData(albumFromIntent)
        }
        else{
            finishWithResultData(Activity.RESULT_CANCELED){}
        }
    }

    private fun onGoBack(){
        finishWithResultData(Activity.RESULT_OK){
            //Return the index of this album in a Lists if transferred
            val posIdx = intent.getIntExtra(INTENT_KEY_TRANSFER_ALBUM_REFRESH_INDEX,-1)
            putExtra(INTENT_KEY_TRANSFER_ALBUM_REFRESH_INDEX,posIdx)
        }
    }
    //endregion


}