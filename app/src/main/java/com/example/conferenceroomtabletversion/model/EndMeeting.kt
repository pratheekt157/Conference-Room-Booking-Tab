package com.example.conferenceroomtabletversion.model

import com.google.gson.annotations.SerializedName

data class EndMeeting(
    @SerializedName("meetId")
    var bookingId: Int? = 0,

    @SerializedName("startOrEnd")
    var status: Boolean? = null,

    @SerializedName("currentTime")
    var currentTime: String? = null
)