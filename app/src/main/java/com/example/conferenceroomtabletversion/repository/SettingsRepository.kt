package com.example.conferenceroomtabletversion.repository

import com.example.conferenceroomtabletversion.helper.Constants
import com.example.conferenceroomtabletversion.model.Buildings
import com.example.conferenceroomtabletversion.model.ConferenceList
import com.example.conferenceroomtabletversion.service.ResponseListener
import com.example.globofly.services.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsRepository {
    /**
     * this block provides a static method which will return the object of repository
     * if the object is already their than it return the same
     * or else it will return a new object
     */
    companion object {
        var mSettingsRepository: SettingsRepository? = null
        fun getInstance(): SettingsRepository {
            if (mSettingsRepository == null) {
                mSettingsRepository = SettingsRepository()
            }
            return mSettingsRepository!!
        }
    }

    /**
     * Calling the Building Api through retrofit call for the Setting Activity
     */

    fun getBuildingList(listener: ResponseListener) {
        val service = ServiceBuilder.getObject()
        val requestCall: Call<List<Buildings>> = service.getBuildingList()
        requestCall.enqueue(object : Callback<List<Buildings>> {
            override fun onFailure(call: Call<List<Buildings>>, t: Throwable) {
                /**
                 * call interface method which is implemented in ViewModel
                 */
                listener.onFailure(Constants.INTERNAL_SERVER_ERROR)
            }
            override fun onResponse(call: Call<List<Buildings>>, response: Response<List<Buildings>>) {
                if ((response.code() == Constants.OK_RESPONSE) or (response.code() == Constants.SUCCESSFULLY_CREATED)) {
                    /**
                     * call interface method which is implemented in ViewModel
                     */
                    listener.onSuccess(response.body()!!)
                }else {
                    /**
                     * call interface method which is implemented in ViewModel
                     */
                    listener.onFailure(response.code())
                }

            }

        })
    }
    /**
     * Calling the Conference Api of a particular building through retrofit call for the Setting Activity
     */
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