package de.ericmuench.appsfactorytesttask.model.lastfm.error

/**
 * This interface defines functionality for an Error From LastFM, that should be able to create
 * an Error-Message from its data or return an Exception.
 * */
interface ErrorFromLastFm {
    //functions
    fun getLastFmErrorMessage() : String

    fun getLastFmException() : LastFmException
}