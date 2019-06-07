package com.example.conferenceroomtabletversion.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferenceroomtabletversion.model.ConferenceList
import com.example.conferenceroomtabletversion.repository.ConferenceRepository
import com.example.conferenceroomtabletversion.service.ResponseListener

class ConferenceViewModel : ViewModel() {
    var mHrConferenceRoomRepository: ConferenceRepository? = null
    var mHrConferenceRoomList = MutableLiveData<List<ConferenceList>>()
    var mFailureCodeForHrConferenceRoom = MutableLiveData<Any>()

    fun getConferenceRoomList(buildingId: Int) {
        mHrConferenceRoomRepository = ConferenceRepository.getInstance()
        mHrConferenceRoomRepository!!.getConferenceRoomList(buildingId,object : ResponseListener {
            override fun onSuccess(success: Any) {
                mHrConferenceRoomList.value = success as List<ConferenceList>
            }

            override fun onFailure(failure: Any) {
                mFailureCodeForHrConferenceRoom.value = failure
            }

        })
    }

    fun returnConferenceRoomList(): MutableLiveData<List<ConferenceList>> {
        return mHrConferenceRoomList
    }

    fun returnFailureForConferenceRoom(): MutableLiveData<Any> {
        return mFailureCodeForHrConferenceRoom
    }
}