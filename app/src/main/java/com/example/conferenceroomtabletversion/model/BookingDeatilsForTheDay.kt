package com.example.conferenceroomtabletversion.model

import com.google.gson.annotations.SerializedName

data class BookingDeatilsForTheDay (
    @SerializedName("RoomId")
    var roomId : Int? = null,

    @SerializedName("EmailId")
    var email: String? = null,

    @SerializedName("StartTime")
    var fromTime : String? = null,

    @SerializedName("EndTime")
    var toTime : String? = null,

    @SerializedName("BuildingName")
    var buildingName : String? = null,

    @SerializedName("RoomName")
    var roomName : String? = null,

    @SerializedName("Purpose")
    var purpose: String? = null,

    @SerializedName("Status")
    var status: String? = null,

    @SerializedName("MeetId")
    var bookingId: Int? = null,

    @SerializedName("NameOfAttendees")
    var name: List<String>? = null,

    @SerializedName("AttendeesMail")
    var cCMail: List<String>? = null,

    @SerializedName("Amenities")
    var amenities: List<String>? = null,

    @SerializedName("NameOfOrganizer")
    var organizer: String? = null,

    @SerializedName("MeetingInHours")
    var meetingDuration: String? = null
)