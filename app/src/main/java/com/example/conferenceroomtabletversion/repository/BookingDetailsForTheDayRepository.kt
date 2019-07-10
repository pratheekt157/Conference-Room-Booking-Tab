package com.example.conferenceroomtabletversion.repository

import android.annotation.SuppressLint
import android.util.Log
import com.example.conferencerommapp.services.ConferenceService
import com.example.conferencerommapp.utils.GetCurrentTimeInUTC
import com.example.conferenceroomtabletversion.helper.Constants
import com.example.conferenceroomtabletversion.model.*
import com.example.conferenceroomtabletversion.service.ResponseListener
import com.example.globofly.services.ServiceBuilder
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*

class BookingDetailsForTheDayRepository {

    /**
     * this block provides a static method which will return the object of repository
     * if the object is already their than it return the same
     * or else it will return a new object
     */
    companion object {
        private var mBookingDashboardRepository: BookingDetailsForTheDayRepository? = null
        fun getInstance(): BookingDetailsForTheDayRepository {
            if (mBookingDashboardRepository == null) {
                mBookingDashboardRepository = BookingDetailsForTheDayRepository()
            }
            return mBookingDashboardRepository!!
        }
    }


    /**
     * function will make api call for making a booking
     * and call the interface method with data from server
     */
    @SuppressLint("SimpleDateFormat")
    fun getBookingList(roomId: Int, listener: ResponseListener) {
        /**
         * API call using retrofit
         */
        val service = ServiceBuilder.getObject()
        val requestCall: Call<List<BookingDeatilsForTheDay>> = service.getBookings(roomId, GetCurrentTimeInUTC.getCurrentTimeInUTC().split(" ")[0])
        requestCall.enqueue(object : Callback<List<BookingDeatilsForTheDay>> {
            override fun onFailure(call: Call<List<BookingDeatilsForTheDay>>, t: Throwable) {
                listener.onFailure(Constants.INTERNAL_SERVER_ERROR)
            }
            override fun onResponse(call: Call<List<BookingDeatilsForTheDay>>, response: Response<List<BookingDeatilsForTheDay>>) {
                if ((response.code() == Constants.OK_RESPONSE) or (response.code() == Constants.SUCCESSFULLY_CREATED)) {
                    listener.onSuccess(response.body()!!)
                } else {
                    listener.onFailure(response.code())
                }
            }
        })

    }


    /**
     * function will make api call for making a booking
     * and call the interface method with data from server
     */
    fun endMeeting(mEndMeeting: EndMeeting, listener: ResponseListener) {
        /**
         * API call using retrofit
         */
        val service = ServiceBuilder.getObject()
        val requestCall: Call<ResponseBody> = service.endMeeting(mEndMeeting)
        requestCall.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                listener.onFailure(Constants.INTERNAL_SERVER_ERROR)
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if ((response.code() == Constants.OK_RESPONSE) or (response.code() == Constants.SUCCESSFULLY_CREATED)) {
                    listener.onSuccess(response.body()!!)
                } else {
                    listener.onFailure(response.code())
                }
            }
        })
    }

    /**
     * function will make api call for making a booking
     * and call the interface method with data from server
     */
    fun startMeeting(mEndMeeting: EndMeeting, listener: ResponseListener) {
        /**
         * API call using retrofit
         */
        val service = ServiceBuilder.getObject()
        val requestCall: Call<ResponseBody> = service.endMeeting(mEndMeeting)
        requestCall.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                listener.onFailure(Constants.INTERNAL_SERVER_ERROR)
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if ((response.code() == Constants.OK_RESPONSE) or (response.code() == Constants.SUCCESSFULLY_CREATED)) {
                    listener.onSuccess(response.body()!!)
                } else {
                    listener.onFailure(response.code())
                }
            }
        })
    }


    /**
     * function will initialize the MutableLivedata Object and than call a function for api call
     * Passing the Context and model and call API, In return sends the status of LiveData
     */
    fun addBookingDetails(mBooking: NewBookingInput, listener: ResponseListener) {
        /**
         * api call using retorfit
         */
        val service = ServiceBuilder.getObject()
        val requestCall: Call<ResponseBody> = service.addBookingDetails(mBooking)
        requestCall.enqueue(object : Callback<ResponseBody> {

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                listener.onFailure(Constants.INTERNAL_SERVER_ERROR)
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if ((response.code() == Constants.OK_RESPONSE) or (response.code() == Constants.SUCCESSFULLY_CREATED)) {
                    listener.onSuccess(response.code())
                } else {
                    listener.onFailure(response.code())
                }
            }
        })
    }


    /**
     * function will make an API call to make request for the updation of booking
     * and call the interface method with data from server
     */
    fun updateBookingDetails(mUpdateBooking: UpdateBooking, listener: ResponseListener) {
        val service = ServiceBuilder.getObject()
        val requestCall: Call<ResponseBody> = service.update(mUpdateBooking)
        requestCall.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                listener.onFailure(Constants.INTERNAL_SERVER_ERROR)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if ((response.code() == Constants.OK_RESPONSE) or (response.code() == Constants.SUCCESSFULLY_CREATED)) {
                    listener.onSuccess(response.code())
                } else {
                    listener.onFailure(response.code())
                }
            }
        })
    }


    /**
     * function will initialize the MutableLivedata Object and than call a function for api call
     * Passing the Context and model and call API, In return sends the status of LiveData
     */
    fun addFeedback(mFeedback: Feedback, listener: ResponseListener) {
        /**
         * api call using retorfit
         */
        val service = ServiceBuilder.getObject()
        val requestCall: Call<ResponseBody> = service.addFeedback(mFeedback)
        requestCall.enqueue(object : Callback<ResponseBody> {

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                listener.onFailure(Constants.INTERNAL_SERVER_ERROR)
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if ((response.code() == Constants.OK_RESPONSE) or (response.code() == Constants.SUCCESSFULLY_CREATED)) {
                    listener.onSuccess(response.code())
                } else {
                    listener.onFailure(response.code())
                }
            }
        })
    }

    /**
     * make request to server for unblock room
     */
    fun unblockRoom(bookingId: Int, listener: ResponseListener) {
        val unBlockApi = ServiceBuilder.buildService(ConferenceService::class.java)
        val requestCall: Call<ResponseBody> = unBlockApi.unBlockingConferenceRoom(bookingId)
        requestCall.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                listener.onFailure(Constants.INTERNAL_SERVER_ERROR)
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if ((response.code() == Constants.OK_RESPONSE) or (response.code() == Constants.SUCCESSFULLY_CREATED)) {
                    listener.onSuccess(response.body()!!)
                } else {
                    listener.onFailure(response.code())
                }
            }
        })
    }
}





