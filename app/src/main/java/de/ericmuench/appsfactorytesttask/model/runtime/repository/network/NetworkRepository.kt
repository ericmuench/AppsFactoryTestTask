package de.ericmuench.appsfactorytesttask.model.runtime.repository.network

import de.ericmuench.appsfactorytesttask.clerk.network.LastFmApiClient


/**
 * This class defines a Repository that is able to load data from the Network (respectively
 * LastFM-API in this case). To improve performance, caching might be implemented by subclasses.
 * */
abstract class NetworkRepository(protected val apiClient : LastFmApiClient)