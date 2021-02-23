package de.ericmuench.appsfactorytesttask.util.loading

/**
 * This enum should define a loading-state for loading actions
 */
enum class LoadingState {
    IDLE,
    LOADING,
    RELOADING,
    LOADING_MORE;

    //fields
    val isLoading : Boolean
    get() = this == LOADING || this == LOADING_MORE || this == RELOADING
}