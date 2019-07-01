package com.example.conferenceroomtabletversion.model

data class SlotFinalList (
    var slot: String? = null,
    var isBooked: Boolean? = false,
    var status: String? = null,
    var meetingDuration: String? = null,
    var inPast: Boolean? = null
)