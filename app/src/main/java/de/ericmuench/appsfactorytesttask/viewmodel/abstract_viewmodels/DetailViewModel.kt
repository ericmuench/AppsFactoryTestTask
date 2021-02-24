package de.ericmuench.appsfactorytesttask.viewmodel.abstract_viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * This ViewModel provides functionality for a Detail-Screen. Due to the fact, that the Screens for
 * Albums-Detail and Artist-Detail act similar, this class should defines all functionality that is
 * the same.
 * */
abstract class DetailViewModel<T> : ViewModel() {
    //region LiveData
    private val _detailData = MutableLiveData<T>()
    val detailData : LiveData<T>
    get() = _detailData
    //endregion

    //region functions
    /**
     * This function can be used to init the ViewModel-LiveData with a value that comes from
     * the Activity/Fragment that uses this ViewModel. The value is only applied if the value
     * of the LiveData is null
     *
     * @param data The initial data to be applied to the LiveData-fields
     * @return Whether the initialize-Operation was successful or not
     * */
    fun initializeWithTransferredData(data : T) : Boolean{
        if(_detailData.value != null){
             return false
        }

        _detailData.value = data
        return true
    }

    protected fun setDetailDataValue(value : T){
        _detailData.value = value
    }
    //endregion
}