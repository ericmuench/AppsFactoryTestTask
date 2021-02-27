package de.ericmuench.appsfactorytesttask.model.repository.network

import android.content.Context
import de.ericmuench.appsfactorytesttask.clerk.network.LastFmApiClient
import de.ericmuench.appsfactorytesttask.util.errorhandling.ContextReferenceResourceExceptionGenerator
import de.ericmuench.appsfactorytesttask.util.errorhandling.ResourceThrowableGenerator


/**
 * This class defines a Repository that is able to load data from the Network (respectively
 * LastFM-API in this case). To improve performance, caching might be implemented by subclasses.
 * */
abstract class NetworkRepository(
    protected val apiClient : LastFmApiClient,
    private val context : Context
) : ResourceThrowableGenerator by ContextReferenceResourceExceptionGenerator(context)