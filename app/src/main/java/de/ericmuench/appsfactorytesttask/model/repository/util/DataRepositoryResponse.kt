package de.ericmuench.appsfactorytesttask.model.repository.util

/**
 * This sealed class defines a response from the DataRepository
 * */
sealed class DataRepositoryResponse<out D, out E>{
    data class Data<T>(val value : T) : DataRepositoryResponse<T, Nothing>()
    data class Error(val error : Throwable) : DataRepositoryResponse<Nothing, Throwable>()
}