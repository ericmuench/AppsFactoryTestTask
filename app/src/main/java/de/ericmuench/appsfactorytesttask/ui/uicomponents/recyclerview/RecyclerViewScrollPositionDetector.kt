package de.ericmuench.appsfactorytesttask.ui.uicomponents.recyclerview

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.ericmuench.appsfactorytesttask.ui.uicomponents.scrolling.ScrollPositionDetector
import de.ericmuench.appsfactorytesttask.util.extensions.castedAs
import de.ericmuench.appsfactorytesttask.util.extensions.lastItemIndex

/**
 * This class can detect the position of a RecyclerView according to Scroll-Events and execute
 * certain actions.
 *
 * For now, just the detection of the end-position is implemented.
 *
 * CAUTION: This class only works with a LinearLayoutManager or with other LayoutManagers that
 * inherit from LinearLayoutManager.
 */
class RecyclerViewScrollPositionDetector : RecyclerView.OnScrollListener(), ScrollPositionDetector {

    //Events
    /**This functional type field is triggered if the end of the recyclerview is reached by scrolling*/
    var onEndReached : () -> Unit= {}


    //Functions for the ScrollListener
    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        recyclerView.layoutManager.castedAs<LinearLayoutManager> { layoutManager ->
            val adapter = recyclerView.adapter  ?: return@castedAs

            if (isEndPosition(recyclerView,layoutManager,newState,adapter)) {
                notifyEndReached()
            }
        }
    }

    //region Interface Functions
    override fun notifyEndReached() = onEndReached.invoke()

    //endregion

    //Help Functions
    /**
     * This function can check whether the end position of a RecyclerView has been reached.
     *
     * @param layoutManager The LayoutManager of the RecyclerView (get access to the last position)
     * @param scrollState The ScrollState of the RecyclerView (check if the user is currently
     * scrolling or not)
     * @param adapter The Adapter of the Recyclerview (get access to how much data is currently stored)
     *
     * @return Whether the endof the RecyclerView is reached
     */
    private fun isEndPosition(
            recyclerView: RecyclerView,
            layoutManager: LinearLayoutManager,
            scrollState: Int,
            adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    ) : Boolean{
        val adapterLastIndex = adapter.lastItemIndex().takeIf { it >= 0 } ?: return false
        val lastLayoutManagerPosition = layoutManager.findLastCompletelyVisibleItemPosition()
        println("debug: Last Adapter Item Index is: $adapterLastIndex. Last Position is: ${lastLayoutManagerPosition}")
        //return lastLayoutManagerPosition == adapterLastIndex
        val lastView = recyclerView.findViewHolderForAdapterPosition(adapterLastIndex)?.itemView ?: return false
        val result = layoutManager.isViewPartiallyVisible(lastView,true,true)
        println("debug: Is End Position: $result")
        return result
    }
}