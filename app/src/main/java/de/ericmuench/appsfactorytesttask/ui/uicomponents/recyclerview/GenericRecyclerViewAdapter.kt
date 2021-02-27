package de.ericmuench.appsfactorytesttask.ui.uicomponents.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import de.ericmuench.appsfactorytesttask.R

/**
 * This class defines a Recyclerview-Adapter for generic purposes to bind arbitrary elements to
 * a simple Item. For this it uses an Extension Function for binding data to a ViewHolder.
 * This can be done with onApplyDataToViewHolder.
 *
 */
abstract class GenericRecyclerViewAdapter<Data,VH: RecyclerView.ViewHolder>(
    protected val context : Context,
    adapterData : Iterable<Data>
) : RecyclerView.Adapter<VH>() {

    //fields
    /**This field stores the data for this adapter*/
    private val data = adapterData.toMutableList()

    /**
     * This field handles the bind operation for the ViewHolder and is called in @see onBindViewHolder.
     * It should allow the access to the Adapter within the closure
     * of onApplyDataToViewHolder of the class. Further to that, you can use the ViewHolder, the current
     * element and the positions to apply your data to the basic UI-Components of the ViewHolder as
     * you like.
     * */
    private var onApplyDataToViewHolder : GenericRecyclerViewAdapter<Data,VH>.(VH, Data, Int) -> Unit = {_,_,_ -> }

    //Functions for Adapter

    override fun onBindViewHolder(holder: VH, position: Int) {
        val element = data[position]
        onApplyDataToViewHolder.invoke(this,holder,element,position)
    }

    override fun getItemCount(): Int = data.size

    //Functions
    /**
     * This function defines a FluentApi-Style-Setter for @see onApplyDataToViewHolder.
     * @param applyFunc The value that should be contained in @see onApplyDataToViewHolder
     */
    fun setOnApplyDataToViewHolder(applyFunc : GenericRecyclerViewAdapter<Data,VH>.(VH, Data, Int) -> Unit) {
        onApplyDataToViewHolder = applyFunc
    }

    /**
     * This function adds an element to the data of this adapter.
     * @param element The Element to be added
     * @return Whether the add-operation was successful or not
     */
    fun addElement(element : Data) : Boolean{
        data.add(element)
        notifyItemInserted(data.lastIndex)
        return true
    }

    /**
     * This function adds multiple elements to the data of this adapter.
     * @param elements The Elements to be added
     * @return Whether the add-operation was successful or not
     */
    fun addElements(elements : Collection<Data>) : Boolean{
        if(elements.isEmpty()){
            return false
        }
        val oldLastIndex = data.lastIndex
        val success = data.addAll(elements)
        if(success){
            notifyItemRangeInserted(oldLastIndex+1,data.lastIndex)
        }
        return success
    }


    /**
     * This function clears the elements of the adapter.
     * @return Whether the clear-Operation was successful
     */
    fun clearElements() : Boolean{
        if(data.isEmpty()){
            return false
        }

        data.clear()
        notifyDataSetChanged()
        return true
    }
}

