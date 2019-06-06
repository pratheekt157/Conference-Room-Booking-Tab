package com.example.conferenceroomtabletversion.model

import com.google.gson.annotations.SerializedName

data class UpdateBooking(

//    @SerializedName("NewStartTime")
//    var newFromTime: String? = null,

    @SerializedName("NewEndTime")
    var newtotime: String? = null,

    @SerializedName("MeetId")
    var bookingId: Int? = null,

    @SerializedName("NewStartTime")
    var newStartTime: String? = null,

    @SerializedName("ExtendMeeting")
    val status: Boolean = true
)