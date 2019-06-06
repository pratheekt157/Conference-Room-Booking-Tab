package com.example.conferenceroomtabletversion.model

import com.google.gson.annotations.SerializedName

data class NewBookingInput(

    @SerializedName("Purpose")
    var eventName: String? = null,

    @SerializedName("Passcode")
    var passcode: Int? = null,

    @SerializedName("StartTime")
    var startTime: String? = null,

    @SerializedName("endTime")
    var endTime: String? = null,

    @SerializedName("RoomId")
    var roomId: Int? = null,

    @SerializedName("BuildingId")
    var buildingId: Int? = null
)