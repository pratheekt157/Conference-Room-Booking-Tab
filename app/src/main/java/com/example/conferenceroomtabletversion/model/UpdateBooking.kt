package com.example.conferenceroomtabletversion.model

import com.google.gson.annotations.SerializedName

data class UpdateBooking(

//    @SerializedName("NewStartTime")
//    var newFromTime: String? = null,

    @SerializedName("newEndTime")
    var newtotime: String? = null,

    @SerializedName("meetId")
    var bookingId: Int? = null,

    @SerializedName("newStartTime")
    var newStartTime: String? = null,

    @SerializedName("extendMeeting")
    val status: Boolean = true
)