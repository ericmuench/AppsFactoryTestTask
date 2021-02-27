package de.ericmuench.appsfactorytesttask.util.errorhandling

/**
 * This interface defines functionality for creating a Throwable (usually an Exception) with the
 * message of a resource-String.
 * */
interface ResourceThrowableGenerator {

    /**
     * This function should create a throwable from a given resource ID.
     * @param resMsgId The String-ID for the Message of the Throwable
     *
     * @return A Throwable containing the message associated with resMsgId
     * */
    fun createThrowable(resMsgId : Int) : Throwable
}