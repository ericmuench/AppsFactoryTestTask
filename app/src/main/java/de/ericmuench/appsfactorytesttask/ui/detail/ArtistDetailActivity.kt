package de.ericmuench.appsfactorytesttask.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import de.ericmuench.appsfactorytesttask.R
import de.ericmuench.appsfactorytesttask.app.constants.INTENT_KEY_SEARCH_ARTIST_TO_ARTIST_DETAIL_TRANSFERRED_ARTIST
import de.ericmuench.appsfactorytesttask.model.runtime.Artist
import de.ericmuench.appsfactorytesttask.util.extensions.notNull
import de.ericmuench.appsfactorytesttask.viewmodel.ArtistDetailViewModel


class ArtistDetailActivity : DetailActivity() {

    //region fields
    private val viewModel : ArtistDetailViewModel by viewModels()
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*title = "Hallo Welt"
        setDescriptionHeadline("Hallo Description Headline")
        setDescription("Lorem ipsum")
        setDataHeadline("Hallo Data Headline")

        setMoreButtonOnClickListener {
            if(testVisibility){
                hideFabAction()
                hideDescriptionProgressBar()
                hideDataProgressBar()
            }
            else{
                showFabAction()
                showDataProgressBar()
                showDescriptionProgressBar()
            }
            testVisibility = !testVisibility
        }

        val dummy = List(20){"Item ${it+1}"}
        val adapter = GenericSimpleItemAdapter<String>(this,dummy)
            .onApplyDataToViewHolder { holder, str, _ ->
                holder.txtText.text = str
            }

        setRecyclerViewAdapter(adapter)



        //val draw = ResourcesCompat.getDrawable(resources,R.drawable.ic_album,null)
        //imgViewDetail.setImageDrawable(draw)

        setFabActionOnClickListener {
            Toast.makeText(this,"MORE!",Toast.LENGTH_SHORT).show()
            adapter.addElements(List(5){"Item ${it+1+adapter.itemCount}"})
        }
        setFabActionIconDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_search,null))*/

        //layout setup
        setupLayout()

        //vm setup
        setupViewModel()

        //getting data from intent
        handleIntentData()
    }



    //region Functions for setup Layout
    private fun setupLayout(){
        setupHeadlines()
        hideFabAction()

        //more btn
        setMoreButtonOnClickListener {
            viewModel.detailData.value?.onlineUrl.notNull { link -> openWebUrl(link) }
        }
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
                setDescription(artist.description)
                //TODO: Recyclerview Data
                //TODO: Handle Image with glide
            }

            val onlineUrlAvailable = viewModel.detailData.value?.onlineUrl != null
            setMoreButtonIsEnabled(onlineUrlAvailable)
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
                //TODO: Start loading data
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