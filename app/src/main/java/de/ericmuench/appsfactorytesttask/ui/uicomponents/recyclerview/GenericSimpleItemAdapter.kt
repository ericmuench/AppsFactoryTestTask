package de.ericmuench.appsfactorytesttask.ui.uicomponents.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import de.ericmuench.appsfactorytesttask.R

//TODO: Documentation
class GenericSimpleItemAdapter<T>(
        private val context : Context,
        adapterData : Iterable<T>
    ) : RecyclerView.Adapter<GenericSimpleItemAdapter.GenericSimpleItemViewHolder>() {

    //fields
    /**This field stores the data for this adapter*/
    private val data = adapterData.toMutableList()

    /**
     * This field handles the bind operation for the ViewHolder and is called in @see onBindViewHolder.
     * */
    private var onApplyDataToViewHolder : (GenericSimpleItemViewHolder,T) -> Unit = {_,_ -> }

    //Functions for Adapter
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericSimpleItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.generic_simple_item,parent,false)
        return GenericSimpleItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: GenericSimpleItemViewHolder, position: Int) {
        val element = data[position]
        onApplyDataToViewHolder.invoke(holder,element)
    }

    override fun getItemCount(): Int = data.size

    //Functions
    /**
     * This function defines a FluentApi-Style-Setter for @see onApplyDataToViewHolder.
     * @param applyFunc The value that should be contained in @see onApplyDataToViewHolder
     * @return this object to provide Fluent-Api-Syntax
     */
    fun onApplyDataToViewHolder(applyFunc : (GenericSimpleItemViewHolder,T) -> Unit)
        : GenericSimpleItemAdapter<T> {
        onApplyDataToViewHolder = applyFunc
        return this
    }

    /**
     * This function adds an element to the data of this adapter.
     * @param element The Element to be added
     * @return Whether the add-operation was successful or not
     */
    fun addElement(element : T) : Boolean{
        data.add(element)
        notifyItemInserted(data.lastIndex)
        return true
    }

    /**
     * This function adds multiple elements to the data of this adapter.
     * @param elements The Elements to be added
     * @return Whether the add-operation was successful or not
     */
    fun addElements(elements : Collection<T>) : Boolean{
        val oldLastIndex = data.lastIndex
        val success = data.addAll(elements)
        if(success){
            notifyItemRangeInserted(oldLastIndex+1,data.lastIndex)
        }
        return success
    }


    //ViewHolder Class
    class GenericSimpleItemViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val cardView: CardView = view.findViewById(R.id.card_generic_simple_item)
        val txtText: TextView = view.findViewById(R.id.txt_text_generic_simple_item)
        val materialButtonTogleGroup: MaterialButtonToggleGroup = view.findViewById(R.id.mbtg_generic_simple_item)
        val btnIcon: MaterialButton = view.findViewById(R.id.btn_icon_generic_simple_item)
    }
}