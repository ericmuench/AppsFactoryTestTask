package de.ericmuench.appsfactorytesttask.ui.uicomponents.recyclerview.adapter.legacy

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import de.ericmuench.appsfactorytesttask.R


/**
 * This class defines a Recyclerview Adapter for a Imaged Item as available in generic_imaged_item.xml
 * */
@Deprecated("This class was used in an older Version of this App for RecyclerViews and " +
        "is now replaced by GenericImagedItemListAdapter. It can still be used as a fallback if " +
        "the newer solution has bugs or a wrong behaviour.")
class GenericImagedItemAdapter<T>(
    context: Context,
    data: Iterable<T>
): GenericRecyclerViewAdapter<T, GenericImagedItemAdapter.GenericImagedItemViewHolder>(context,data) {

    //region Overridden Functions from Superclasses
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericImagedItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.generic_imaged_item,parent,false)
        return GenericImagedItemViewHolder(view)
    }
    //endregion

    ///region ViewHolder Class
    class GenericImagedItemViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val cardView : CardView = view.findViewById(R.id.card_generic_imaged_item)
        val imageView : ImageView = view.findViewById(R.id.img_generic_imaged_item)
        val txtTitle : TextView = view.findViewById(R.id.txt_title_generic_imaged_item)
        val txtSubTitle : TextView = view.findViewById(R.id.txt_subtitle_generic_imaged_item)
    }
    //endregion
}