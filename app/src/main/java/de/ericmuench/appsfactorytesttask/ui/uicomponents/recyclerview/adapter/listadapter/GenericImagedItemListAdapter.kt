package de.ericmuench.appsfactorytesttask.ui.uicomponents.recyclerview.adapter.listadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.ericmuench.appsfactorytesttask.R

/**
 * This class defines a Recyclerview-List-Adapter for a Imaged Item as available in generic_imaged_item.xml
 * */
class GenericImagedItemListAdapter<T>(
    context: Context,
    diffUtilCallback : DiffUtil.ItemCallback<T>,
) : GenericListAdapter<T, GenericImagedItemListAdapter.GenericImagedListItemViewHolder>(diffUtilCallback,context) {

    //region Overridden Functions from Superclasses
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericImagedListItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.generic_imaged_item,parent,false)
        return GenericImagedListItemViewHolder(view)
    }
    //endregion

    ///region ViewHolder Class
    class GenericImagedListItemViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val cardView : CardView = view.findViewById(R.id.card_generic_imaged_item)
        val imageView : ImageView = view.findViewById(R.id.img_generic_imaged_item)
        val txtTitle : TextView = view.findViewById(R.id.txt_title_generic_imaged_item)
        val txtSubTitle : TextView = view.findViewById(R.id.txt_subtitle_generic_imaged_item)
    }
    //endregion
}