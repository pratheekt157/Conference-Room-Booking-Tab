package com.example.conferenceroomtabletversion.model

import com.google.gson.annotations.SerializedName
import java.time.Instant
import java.util.*

data class BookingForTheDayRequest (

    @SerializedName("Date")
    var currentDate: Date? = null
)