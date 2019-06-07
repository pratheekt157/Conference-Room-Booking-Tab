package com.example.conferenceroomtabletversion.repository

import com.example.conferenceroomtabletversion.helper.Constants
import com.example.conferenceroomtabletversion.model.Buildings
import com.example.conferenceroomtabletversion.service.ResponseListener
import com.example.globofly.services.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BuildingsRepository {
    /**
     * this block provides a static method which will return the object of repository
     * if the object is already their than it return the same
     * or else it will return a new object
     */
    companion object {
        var mBuildingsRepository: BuildingsRepository? = null
        fun getInstance(): BuildingsRepository {
            if (mBuildingsRepository == null) {
                mBuildingsRepository = BuildingsRepository()
            }
            return mBuildingsRepository!!
        }
    }

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

}