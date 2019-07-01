package com.example.conferenceroomtabletversion.model

import com.google.gson.annotations.SerializedName

data class BookingDeatilsForTheDay (
    @SerializedName("roomId")
    var roomId : Int? = null,

    @SerializedName("emailId")
    var email: String? = null,

    @SerializedName("startTime")
    var fromTime : String? = null,

    @SerializedName("endTime")
    var toTime : String? = null,

    @SerializedName("buildingName")
    var buildingName : String? = null,

    @SerializedName("roomName")
    var roomName : String? = null,

    @SerializedName("purpose")
    var purpose: String? = null,

    @SerializedName("status")
    var status: String? = null,

    @SerializedName("meetId")
    var bookingId: Int? = null,

    @SerializedName("nameOfAttendees")
    var name: List<String>? = null,

    @SerializedName("attendeesMail")
    var cCMail: List<String>? = null,

    @SerializedName("amenities")
    var amenities: List<String>? = null,

    @SerializedName("nameOfOrganizer")
    var organizer: String? = null,

    @SerializedName("meetingInHours")
    var meetingDuration: String? = null
)