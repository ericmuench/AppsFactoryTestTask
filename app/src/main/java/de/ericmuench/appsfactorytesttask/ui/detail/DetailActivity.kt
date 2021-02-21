package de.ericmuench.appsfactorytesttask.ui.detail

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
        setupDescriptionTextView()
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

    private fun setupDescriptionTextView() = with(viewBinding){
        txtDescriptionDetail.isClickable = true
        txtDescriptionDetail.movementMethod = LinkMovementMethod.getInstance();
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

    val imgViewDetail : ImageView
    get() = viewBinding.imgDetail

    protected fun setFabActionOnClickListener(listener: (View?) -> Unit) = with(viewBinding){
        if(runsInLandscape()){
            imgBtnFabSubstituteLand?.setOnClickListener(listener)
        }

        fabDetail?.setOnClickListener(listener)
    }

    protected fun setFabActionIconDrawable(drawable : Drawable?) = with(viewBinding){
        if(runsInLandscape()){
            imgBtnFabSubstituteLand?.setImageDrawable(drawable)
        }

        fabDetail?.setImageDrawable(drawable)
    }

    protected fun hideFabAction()= with(viewBinding){
        if(runsInLandscape()){
            imgBtnFabSubstituteLand?.visibility = View.INVISIBLE
        }

        fabDetail?.visibility = View.GONE
    }

    protected fun showFabAction()= with(viewBinding){
        if(runsInLandscape()){
            imgBtnFabSubstituteLand?.visibility = View.VISIBLE
        }

        fabDetail?.visibility = View.VISIBLE
    }

    protected fun setDescriptionHeadline(headline : CharSequence)= with(viewBinding){
        txtHeadlineDescriptionDetail.text = headline
    }

    protected fun setDescription(description: CharSequence) = with(viewBinding){
        txtDescriptionDetail.text = HtmlCompat.fromHtml(description.toString(),HtmlCompat.FROM_HTML_OPTION_USE_CSS_COLORS)
    }

    protected fun setDataHeadline(headline : CharSequence)= with(viewBinding){
        txtHeadlineDataDetail.text = headline
    }

    protected fun setMoreButtonOnClickListener(listener : (View?) -> Unit) = with(viewBinding){
        btnMoreDetail.setOnClickListener(listener)
    }

    protected fun setMoreButtonIsEnabled(isEnabled : Boolean) = with(viewBinding){
        btnMoreDetail.isEnabled = isEnabled
    }

    protected fun <VH: RecyclerView.ViewHolder> setRecyclerViewAdapter(adapter: RecyclerView.Adapter<VH>){
        viewBinding.recyclerViewDataDetail.adapter = adapter
    }

    protected fun hideDescriptionProgressBar() = viewBinding.progressDescriptionDetail.hide()
    protected fun showDescriptionProgressBar() = viewBinding.progressDescriptionDetail.show()
    protected fun hideDataProgressBar() = viewBinding.progressDataDetail.hide()
    protected fun showDataProgressBar() = viewBinding.progressDataDetail.show()
    protected fun hideAllProgressbars() {
        hideDataProgressBar()
        hideDescriptionProgressBar()
    }
    //endregion



}