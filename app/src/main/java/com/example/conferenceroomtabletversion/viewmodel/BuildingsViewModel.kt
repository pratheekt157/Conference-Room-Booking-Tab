package com.example.conferenceroomtabletversion.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferenceroomtabletversion.model.Buildings
import com.example.conferenceroomtabletversion.repository.BuildingsRepository
import com.example.conferenceroomtabletversion.service.ResponseListener


class BuildingViewModel : ViewModel() {

    /**
     * a object which will hold the reference to the corrosponding repository class
     */
    var mBuildingsRepository: BuildingsRepository? = null

    /**
     * A MutableLivedata variable which will hold the Value for positive response from repository
     */
    var mBuildingList =  MutableLiveData<List<Buildings>>()

    /**
     * A MutableLiveData variable which will hold the Value for negative response from repository
     */
    var errorCodeFromServer =  MutableLiveData<Any>()

    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will update the values of MutableLiveData objects according to the response from server
     */
    fun getBuildingList() {
        mBuildingsRepository = BuildingsRepository.getInstance()
        mBuildingsRepository!!.getBuildingList(object: ResponseListener {
            override fun onFailure(failure: Any) {
                errorCodeFromServer.value = failure
            }
            override fun onSuccess(success: Any) {
                mBuildingList!!.value = success as List<Buildings>
            }

        })
    }

    /**
     * function will return the MutableLiveData of List of buildings
     */
    fun returnMBuildingSuccess(): MutableLiveData<List<Buildings>> {
        return mBuildingList
    }

    /**
     * function will return the MutableLiveData of Int if something went wrong at server
     */
    fun returnMBuildingFailure(): MutableLiveData<Any> {
        return errorCodeFromServer
    }
}