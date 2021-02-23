package de.ericmuench.appsfactorytesttask.ui.uicomponents.scrolling

import android.view.View
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import de.ericmuench.appsfactorytesttask.util.extensions.notNull


class NestedScrollViewPositionDetector : ScrollPositionDetector, NestedScrollView.OnScrollChangeListener {
    //Events
    /**This functional type field is triggered if the end of the NestedScrollView is reached by scrolling*/
    var onEndReached : () -> Unit= {}

    //region Interface functions
    override fun notifyEndReached() = onEndReached.invoke()

    override fun onScrollChange(
        v: NestedScrollView?,
        scrollX: Int,
        scrollY: Int,
        oldScrollX: Int,
        oldScrollY: Int
    ) {
        println("On Scroll change")
        v.notNull { nestedScrollView ->

            //calculation was made possible by this tutorial:
            //https://www.tutorialspoint.com/how-to-detect-end-of-scrollview-in-android
            val view: View = nestedScrollView.getChildAt(nestedScrollView.childCount - 1)
            val bottomDetector: Int =
                view.bottom - (nestedScrollView.height + nestedScrollView.scrollY)
            if (bottomDetector == 0) {
                notifyEndReached()
            }
        }

    }
    //endregion
}