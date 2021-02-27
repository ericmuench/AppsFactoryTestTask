package de.ericmuench.appsfactorytesttask.ui.uicomponents.recyclerview.adapter.listadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.ericmuench.appsfactorytesttask.R


/**
 * This class defines a Recyclerview-List-Adapter for a Simple Item as available in generic_simple_item.xml
 * */
class GenericSimpleItemListAdapter<T>(
    context : Context,
    diffCallback : DiffUtil.ItemCallback<T>
) : GenericListAdapter<T, GenericSimpleItemListAdapter.GenericSimpleItemListViewHolder>(diffCallback,context) {
    //Functions for Adapter
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericSimpleItemListViewHolder {
        val view = LayoutInflater
            .from(context)
            .inflate(R.layout.generic_simple_item,parent,false)
        return GenericSimpleItemListViewHolder(view)
    }

    //ViewHolder Class
    class GenericSimpleItemListViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val cardView: CardView = view.findViewById(R.id.card_generic_simple_item)
        val txtText: TextView = view.findViewById(R.id.txt_text_generic_simple_item)
        val imageButton: ImageButton = view.findViewById(R.id.img_btn_generic_simple_item)
    }

}