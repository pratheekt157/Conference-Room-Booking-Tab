package com.example.conferencerommapp.services

import com.example.conferenceroomtabletversion.model.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import java.util.*

interface ConferenceService {
    @GET("api/BookingsForTheDay")
    fun getBookings(
       @Query("RoomId") roomI: Int,
        //@Body bookingList: BookingList
       @Query("TodaysDate") date: String
    ): Call<List<BookingDeatilsForTheDay>>

    @PUT("api/MeetingStatus")
    fun endMeeting(
        @Body endMeeting: EndMeeting
    ): Call<ResponseBody>

    @POST("api/BookRoom")
    fun addBookingDetails(
        @Body booking: NewBookingInput
    ): Call<ResponseBody>

    @PUT("api/UpdateBooking")
    fun update(
        @Body updateBooking: UpdateBooking
    ): Call<ResponseBody>

    @POST("api/SubmitFeedback")
    fun addFeedback(
        @Body feedback: Feedback
    ): Call<ResponseBody>

    @GET("api/Building")
    fun getBuildingList():Call<List<Buildings>>

    @GET("api/ConferenceRooms")
    fun conferencelist(
            @Query("buildingId") id: Int
    ): Call<List<ConferenceList>>

    @PUT("api/UnblockRoom")
    fun unBlockingConferenceRoom(
        @Body meetId: Int
    ): Call<ResponseBody>

}