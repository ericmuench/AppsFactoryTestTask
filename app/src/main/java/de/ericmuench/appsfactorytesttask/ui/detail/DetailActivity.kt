package de.ericmuench.appsfactorytesttask.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.ericmuench.appsfactorytesttask.R
import de.ericmuench.appsfactorytesttask.databinding.ActivityDetailBinding
import de.ericmuench.appsfactorytesttask.ui.uicomponents.abstract_activities_fragments.BaseActivity
import de.ericmuench.appsfactorytesttask.util.extensions.runsInLandscape

/**
 * This Activity provides functionality for setting values to the UI of a Detail-Screen,
 * due to the fact that Detail-Screens for Albums and Artists look similar.
 * */
abstract class DetailActivity : BaseActivity() {

    //region fields
    private lateinit var viewBinding : ActivityDetailBinding
    //endregion

    //region lifecycle functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //ViewBinding
        viewBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        //setup Layout
        setupToolbar()
        setupRecyclerView()
    }
    //endregion


    //region help functions for Layout setup
    private fun setupToolbar() = with(viewBinding){
        //portrait
        setSupportActionBar(toolbarDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //landscape
        imgBtnBackDetailLand?.setOnClickListener{
            onActionbarBackButtonPressed()
        }
    }

    private fun setupRecyclerView() = with(viewBinding){
        recyclerViewDataDetail.layoutManager = LinearLayoutManager(this@DetailActivity)
    }
    //endregion

    //region Functions + computed properties for applying data to Layout components
    override fun setTitle(title: CharSequence) = with(viewBinding){
        super.setTitle(title)

        if(runsInLandscape()){
            txtTitleDetailLand?.text = title
        }

        collapsingToolbarLayoutDetail?.title = title
    }

    val imgView : ImageView
    get() = viewBinding.imgDetail

    protected fun setDescriptionHeadline(headline : CharSequence)= with(viewBinding){
        txtHeadlineDescriptionDetail.text = headline
    }

    protected fun setDescription(description: CharSequence) = with(viewBinding){
        txtDescriptionDetail.text = description
    }

    protected fun setDataHeadline(headline : CharSequence)= with(viewBinding){
        txtHeadlineDataDetail.text = headline
    }

    protected fun setMoreButtonOnClickListener(listener : (View?) -> Unit) = with(viewBinding){
        btnMoreDetail.setOnClickListener {
            listener(it)
        }
    }

    protected fun <VH: RecyclerView.ViewHolder> setRecyclerViewAdapter(adapter: RecyclerView.Adapter<VH>){
        viewBinding.recyclerViewDataDetail.adapter = adapter
    }
    //endregion



}