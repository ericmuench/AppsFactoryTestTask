package de.ericmuench.appsfactorytesttask.ui.detail

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import de.ericmuench.appsfactorytesttask.R
import de.ericmuench.appsfactorytesttask.app.AppsFactoryTestTaskApplication
import de.ericmuench.appsfactorytesttask.app.constants.INTENT_KEY_TRANSFER_ALBUM
import de.ericmuench.appsfactorytesttask.model.runtime.Album
import de.ericmuench.appsfactorytesttask.model.runtime.Song
import de.ericmuench.appsfactorytesttask.ui.uicomponents.recyclerview.GenericSimpleItemAdapter
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

    private var recyclerViewAdapter : GenericSimpleItemAdapter<Song>? = null
    //endregion


    //region Lifecycle Functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //setup Layout
        setupLayout()

        //setup ViewModel
        setupViewModel()

        //apply intent data
        applyIntentDataOrFinish()

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
        recyclerViewAdapter = GenericSimpleItemAdapter<Song>(this, emptyList()).apply {
            setOnApplyDataToViewHolder { holder, song, _ ->
                holder.cardView.setOnClickListener {
                    song.onlineUrl.notNull {songUrl ->
                        openWebUrl(songUrl)
                    }
                }
                holder.imageButton.visibility = View.GONE
                holder.txtText.text = song.title
            }
        }

        recyclerView.adapter = recyclerViewAdapter
    }

    private fun setupHeadlines(){
        setDescriptionHeadline(resources.getString(R.string.about_album))
        setDataHeadline(resources.getString(R.string.songs))
    }

    private fun setupFabAction(){
        setFabActionOnClickListener {
            //TODO: Register Click Action for storing album
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
                            .into(imgViewDetail)
                    }
                }


                val description = album
                    .description
                    .takeIf { it.isNotBlank() }
                    ?: resources.getString(R.string.no_description_available)
                setDescription(description)
                if(recyclerViewAdapter?.itemCount == 0){
                    recyclerViewAdapter?.addElements(album.songs)
                }

            }
        }
    }
    //endregion

    //region Intent Handling
    private fun applyIntentDataOrFinish(){
        val albumFromIntent = intent?.getParcelableExtra<Album>(INTENT_KEY_TRANSFER_ALBUM)

        if(albumFromIntent != null){
            viewModel.initializeWithTransferredData(albumFromIntent)
        }
        else{
            finish()
        }
    }
    //endregion


}