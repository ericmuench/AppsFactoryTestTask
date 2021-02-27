package de.ericmuench.appsfactorytesttask.ui.uicomponents.recyclerview.adapter.listadapter

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class GenericListAdapter<Data,VH : RecyclerView.ViewHolder>(
    diffUtilCallback : DiffUtil.ItemCallback<Data>,
    protected val context : Context,
) : ListAdapter<Data,VH>(diffUtilCallback) {

    //region fields

    /**
     * This field handles the bind operation for the ViewHolder and is called in @see onBindViewHolder.
     * It should allow the access to the Adapter within the closure
     * of onApplyDataToViewHolder of the class. Further to that, you can use the ViewHolder, the current
     * element and the positions to apply your data to the basic UI-Components of the ViewHolder as
     * you like.
     * */
    private var onApplyDataToViewHolder : GenericListAdapter<Data, VH>.(VH, Data, Int) -> Unit = { _, _, _ -> }
    //endregion

    //region Adapter Functions
    override fun onBindViewHolder(holder: VH, position: Int) {
        val element = getItem(position) ?: return
        onApplyDataToViewHolder.invoke(this,holder,element,position)
    }
    //endregion


    //region Functions
    /**
     * This function defines a FluentApi-Style-Setter for @see onApplyDataToViewHolder.
     * @param applyFunc The value that should be contained in @see onApplyDataToViewHolder
     */
    fun setOnApplyDataToViewHolder(applyFunc : GenericListAdapter<Data, VH>.(VH, Data, Int) -> Unit) {
        onApplyDataToViewHolder = applyFunc
    }
    //endregion
}