package com.example.conferenceroomtabletversion.repository

import com.example.conferenceroomtabletversion.helper.Constants
import com.example.conferenceroomtabletversion.model.ConferenceList
import com.example.conferenceroomtabletversion.service.ResponseListener
import com.example.globofly.services.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConferenceRepository {
    companion object {
        private var mConferenceRepository: ConferenceRepository? = null
        fun getInstance(): ConferenceRepository {
            if (mConferenceRepository == null) {
                mConferenceRepository = ConferenceRepository()
            }
            return mConferenceRepository!!
        }
    }

    fun getConferenceRoomList(buildingId: Int,listener: ResponseListener) {
        /**
         * api call using retorfit
         */
        val service = ServiceBuilder.getObject()
        val requestCall: Call<List<ConferenceList>> = service.conferencelist(buildingId)
        requestCall.enqueue(object : Callback<List<ConferenceList>> {
            override fun onFailure(call: Call<List<ConferenceList>>, t: Throwable) {
                listener.onFailure(Constants.INTERNAL_SERVER_ERROR)
            }

            override fun onResponse(call: Call<List<ConferenceList>>, response: Response<List<ConferenceList>>) {
                if ((response.code() == Constants.OK_RESPONSE) or (response.code() == Constants.SUCCESSFULLY_CREATED)) {
                    listener.onSuccess(response.body()!!)
                } else {
                    listener.onFailure(response.code())
                }
            }
        })
    }
}