package com.example.conferenceroomtabletversion.model

import com.google.gson.annotations.SerializedName

data class BookingList (
    @SerializedName("roomId")
    var roomId: Int? = null,

    @SerializedName("todaysDate")
    var currentDateTime: String? = null
)