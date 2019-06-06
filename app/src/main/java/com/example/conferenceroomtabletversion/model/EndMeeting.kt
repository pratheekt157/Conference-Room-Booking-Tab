package com.example.conferenceroomtabletversion.model

import com.google.gson.annotations.SerializedName

data class EndMeeting(
    @SerializedName("MeetId")
    var bookingId: Int? = 0,

    @SerializedName("StartOrEnd")
    var status: Boolean? = null,

    @SerializedName("CurrentTime")
    var currentTime: String? = null
)