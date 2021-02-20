package de.ericmuench.appsfactorytesttask.ui.detail

import android.os.Bundle
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import de.ericmuench.appsfactorytesttask.R
import de.ericmuench.appsfactorytesttask.ui.uicomponents.recyclerview.GenericSimpleItemAdapter

class ArtistDetailActivity : DetailActivity() {
    //TODO: Remove demo code

    var testVisibility = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = "Hallo Welt"
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
        setFabActionIconDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_search,null))
    }
}