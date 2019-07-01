package com.example.conferenceroomtabletversion.model

import com.google.gson.annotations.SerializedName

data class NewBookingInput(

    @SerializedName("purpose")
    var eventName: String? = null,

    @SerializedName("passcode")
    var passcode: String? = null,

    @SerializedName("startTime")
    var startTime: String? = null,

    @SerializedName("endTime")
    var endTime: String? = null,

    @SerializedName("roomId")
    var roomId: Int? = null,

    @SerializedName("buildingId")
    var buildingId: Int? = null
)