package de.ericmuench.appsfactorytesttask.ui.uicomponents.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import de.ericmuench.appsfactorytesttask.R

/**
 * This typealias should facilitate the work with the functional type for the apply event.
 * The type of this typealias should allow the access to the Adapter within the closure
 * of onApplyDataToViewHolder of the class. Further to that, you can use the ViewHolder, the current
 * element and the positions to apply your data to the basic UI-Components of the ViewHolder as
 * you like.
 */
private typealias OnApplyEventListener<T>
        = GenericSimpleItemAdapter<T>.(GenericSimpleItemAdapter.GenericSimpleItemViewHolder, T, Int) -> Unit

/**
 * This class defines a Recyclerview-Adapter for generic purposes to bind arbitrary elements to
 * a simple Item. For this it uses an @see OnApplyEventListener<T> and Fluent-API-Syntax.
 * This can be used to applying data to the ViewHolders UI-Components as you specify it in
 * onApplyDataToViewHolder.
 *
 */
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
    private var onApplyDataToViewHolder : OnApplyEventListener<T> = {_,_,_ -> }

    //Functions for Adapter
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericSimpleItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.generic_simple_item,parent,false)
        return GenericSimpleItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: GenericSimpleItemViewHolder, position: Int) {
        val element = data[position]
        onApplyDataToViewHolder.invoke(this,holder,element,position)
    }

    override fun getItemCount(): Int = data.size

    //Functions
    /**
     * This function defines a FluentApi-Style-Setter for @see onApplyDataToViewHolder.
     * @param applyFunc The value that should be contained in @see onApplyDataToViewHolder
     * @return this object to provide Fluent-Api-Syntax
     */
    fun onApplyDataToViewHolder(applyFunc : OnApplyEventListener<T>)
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

    /**
     * This function clears the elements of the adapter.
     * @return Whether the clear-Operation was successful
     */
    fun clearElements() : Boolean{
        if(data.isEmpty()){
            return false
        }

        val oldLastIndex = data.lastIndex
        data.clear()
        notifyItemRangeRemoved(0,oldLastIndex)
        return true
    }


    //ViewHolder Class
    class GenericSimpleItemViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val cardView: CardView = view.findViewById(R.id.card_generic_simple_item)
        val txtText: TextView = view.findViewById(R.id.txt_text_generic_simple_item)
        val checkBox : CheckBox = view.findViewById(R.id.checkbox_generic_simple_item)
    }
}