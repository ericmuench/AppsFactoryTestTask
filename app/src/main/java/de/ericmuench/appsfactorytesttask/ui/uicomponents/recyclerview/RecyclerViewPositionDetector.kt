package de.ericmuench.appsfactorytesttask.ui.uicomponents.recyclerview

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
class RecyclerViewPositionDetector : RecyclerView.OnScrollListener() {

    //Events
    /**This functional type field is triggered if the end of the recyclerview is reached by scrolling*/
    var onEndReached : () -> Unit= {}


    //Functions for the ScrollListener
    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        recyclerView.layoutManager.castedAs<LinearLayoutManager> { layoutManager ->
            val adapter = recyclerView.adapter  ?: return@castedAs

            if (isEndPosition(layoutManager,newState,adapter)) {
                onEndReached.invoke()
            }
        }
    }

    //Help Functions
    /**
     * This function can check whether the end position of a RecyclerView has been reached.
     *
     * @param layoutManager The LayoutManager of the RecyclerView (get access to the last position)
     * @param scrollState The ScrollState of the RecyclerView (check if the user is currently
     * scrolling or not)
     * @param adapter The Adapter of the Recyclerview (get access to how much data is currently stored)
     *
     * @return Whether the endof the RecyclerView is reached and the RecyclerView does not scroll
     */
    private fun isEndPosition(
            layoutManager: LinearLayoutManager,
            scrollState: Int,
            adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    ) : Boolean{
        val adapterLastIndex = adapter.lastItemIndex().takeIf { it >= 0 } ?: return false
        return layoutManager.findLastCompletelyVisibleItemPosition() == adapterLastIndex &&
                scrollState == RecyclerView.SCROLL_STATE_IDLE
    }
}