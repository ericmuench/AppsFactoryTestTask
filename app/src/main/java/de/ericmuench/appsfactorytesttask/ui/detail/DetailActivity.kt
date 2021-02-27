package de.ericmuench.appsfactorytesttask.ui.detail

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.core.text.HtmlCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.ericmuench.appsfactorytesttask.R
import de.ericmuench.appsfactorytesttask.databinding.ActivityDetailBinding
import de.ericmuench.appsfactorytesttask.ui.uicomponents.abstract_activities_fragments.BaseActivity
import de.ericmuench.appsfactorytesttask.util.extensions.notNull
import de.ericmuench.appsfactorytesttask.util.extensions.runsInLandscape

/**
 * This Activity provides functionality for setting values to the UI of a Detail-Screen,
 * due to the fact that Detail-Screens for Albums and Artists look similar.
 * */
abstract class DetailActivity : BaseActivity() {

    //region fields
    private lateinit var viewBinding : ActivityDetailBinding


    protected var shouldDisplayOptionsMenu : Boolean = true
    private var onReloadButtonClicked : () -> Unit = {}
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

    override fun onStart() {
        super.onStart()
        viewBinding.imgBtnMenuReloadSubstituteLand?.visibility = if (shouldDisplayOptionsMenu){
            View.VISIBLE
        }
        else{
            View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_actionbar_menu,menu)
        //assign menu items
        if(!shouldDisplayOptionsMenu){
            menu?.findItem(R.id.acbar_item_reload_detail)?.isVisible = false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.acbar_item_reload_detail -> {
                onReloadButtonClicked.invoke()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

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

    protected fun setOnReloadButtonClickListener(listener: () -> Unit) = with(viewBinding){
        imgBtnMenuReloadSubstituteLand?.setOnClickListener {
            listener.invoke()
        }
        onReloadButtonClicked = listener
    }

    protected open fun setupRecyclerView() = with(viewBinding){
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

    protected val imgViewDetail : ImageView
        get() = viewBinding.imgDetail

    protected fun hideLayerImage() = with(viewBinding){
        imgLayerDetail.visibility = View.INVISIBLE
    }

    protected fun setNestedScrollViewOnScrollStateChangeListener(
        listener : NestedScrollView.OnScrollChangeListener
    ) = with(viewBinding){
        nestedscrollviewDetail.setOnScrollChangeListener(listener)
    }

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

    protected fun getFabActionDrawable() : Drawable? = with(viewBinding){
        return@with if(runsInLandscape()){
            imgBtnFabSubstituteLand?.drawable
        }
        else{
            fabDetail?.drawable
        }
    }

    protected fun hideFabAction()= with(viewBinding){
        if(runsInLandscape()){
            imgBtnFabSubstituteLand?.visibility = View.GONE
        }

        fabDetail?.visibility = View.GONE
    }

    protected fun showFabAction()= with(viewBinding){
        if(runsInLandscape()){
            imgBtnFabSubstituteLand?.visibility = View.VISIBLE
        }

        fabDetail?.visibility = View.VISIBLE
    }

    protected fun setFabActionEnabled(enabled : Boolean) = with(viewBinding){
        if(runsInLandscape()){
            imgBtnFabSubstituteLand?.isEnabled = enabled
            return@with
        }

        fabDetail?.isEnabled = enabled
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

    protected val recyclerView : RecyclerView
    get() = viewBinding.recyclerViewDataDetail

    protected fun setDescriptionLoading(isLoading : Boolean) = with(viewBinding){
        if(isLoading){
            showDescriptionProgressBar()
            txtDescriptionDetail.visibility = View.GONE
        }
        else{
            hideDescriptionProgressBar()
            txtDescriptionDetail.visibility = View.VISIBLE
        }
    }
    protected fun hideDescriptionProgressBar() = viewBinding.progressDescriptionDetail.root.hide()
    protected fun showDescriptionProgressBar() = viewBinding.progressDescriptionDetail.root.show()
    protected fun hideDataProgressBar() = viewBinding.progressDataDetail.root.hide()
    protected fun hideDataProgressBarBottom() = viewBinding.progressDataBottomDetail.root.hide()
    protected fun showDataProgressBarBottom() = viewBinding.progressDataBottomDetail.root.show()
    protected fun showDataProgressBar() = viewBinding.progressDataDetail.root.show()
    protected fun hideAllProgressbars() {
        hideAllDataProgressBars()
        hideDescriptionProgressBar()
    }
    protected fun hideAllDataProgressBars(){
        hideDataProgressBar()
        hideDataProgressBarBottom()
    }
    //endregion



}