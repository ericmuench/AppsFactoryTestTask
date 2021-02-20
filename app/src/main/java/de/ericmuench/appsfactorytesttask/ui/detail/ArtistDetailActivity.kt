package de.ericmuench.appsfactorytesttask.ui.detail

import android.os.Bundle
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import de.ericmuench.appsfactorytesttask.R
import de.ericmuench.appsfactorytesttask.ui.uicomponents.recyclerview.GenericSimpleItemAdapter

class ArtistDetailActivity : DetailActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = "Hallo Welt"
        setDescriptionHeadline("Hallo Description Headline")
        setDescription("Lorem ipsum")
        setDataHeadline("Hallo Data Headline")
        setMoreButtonOnClickListener {
            Toast.makeText(this,"MORE!",Toast.LENGTH_SHORT).show()
        }

        val dummy = List(20){"Item ${it+1}"}
        setRecyclerViewAdapter(GenericSimpleItemAdapter<String>(this,dummy))

        val draw = ResourcesCompat.getDrawable(resources,R.drawable.ic_album,null)
        imgView.setImageDrawable(draw)
    }
}