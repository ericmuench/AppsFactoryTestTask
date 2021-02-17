package de.ericmuench.appsfactorytesttask.util.loading

/**
 * This enum should define a loading-state for loading actions
 */
enum class LoadingState {
    IDLE,
    LOADING,
    RELOADING;

    //fields
    val isLoading : Boolean
    get() = this == LOADING || this == RELOADING
}