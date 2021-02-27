package de.ericmuench.appsfactorytesttask.ui.uicomponents.scrolling


/**
 * This Interface defines functionality for detecting ScrollPositions in several Layouts. For that
 * it provides functions to be implemented by Subclasses that should be executed when a certain
 * Scroll-Position is reached.
 * */
interface ScrollPositionDetector {
    //region functions
    /**This function should notify that the end of the View is reached by scrolling*/
    fun notifyEndReached()
    //endregion
}