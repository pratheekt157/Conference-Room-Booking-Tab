package com.example.conferenceroomtabletversion.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferenceroomtabletversion.model.Buildings
import com.example.conferenceroomtabletversion.model.ConferenceList
import com.example.conferenceroomtabletversion.repository.SettingsRepository
import com.example.conferenceroomtabletversion.service.ResponseListener


class SettingsViewModel : ViewModel() {
    /**
     * a object which will hold the reference to the corrosponding repository class
     */
    var mSettingsRepository: SettingsRepository? = null

    /**
     * A MutableLivedata variable which will hold the Value for positive response from repository
     */
    var mBuildingList =  MutableLiveData<List<Buildings>>()
    var mConferenceRoomList = MutableLiveData<List<ConferenceList>>()

    /**
     * A MutableLiveData variable which will hold the Value for negative response from repository
     */
    var errorCodeFromServer =  MutableLiveData<Any>()
    var mFailureCodeForConferenceRoom = MutableLiveData<Any>()

    //---------------------------------------------------------------------------------------------------------------------
    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will update the values of MutableLiveData objects according to the response from server of Buildings
     */
    fun getBuildingList() {
        mSettingsRepository = SettingsRepository.getInstance()
        mSettingsRepository!!.getBuildingList(object: ResponseListener {
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

    //----------------------------------------------------------------------------------------------------------------------
    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will update the values of MutableLiveData objects according to the response from server of Buildings
     */
    fun getConferenceRoomList(buildingId: Int) {
        mSettingsRepository = SettingsRepository.getInstance()
        mSettingsRepository!!.getConferenceRoomList(buildingId,object : ResponseListener {
            override fun onSuccess(success: Any) {
                mConferenceRoomList.value = success as List<ConferenceList>
            }

            override fun onFailure(failure: Any) {
                mFailureCodeForConferenceRoom.value = failure
            }

        })
    }

    fun returnConferenceRoomList(): MutableLiveData<List<ConferenceList>> {
        return mConferenceRoomList
    }

    fun returnFailureForConferenceRoom(): MutableLiveData<Any> {
        return mFailureCodeForConferenceRoom
    }
}