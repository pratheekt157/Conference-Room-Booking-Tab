package com.example.conferenceroomtabletversion.model

import com.google.gson.annotations.SerializedName

data class ConferenceList (
        @SerializedName("RoomName")
        var roomName : String? = null,

        @SerializedName("Capacity")
        var capacity : Int? = 0,

        @SerializedName("BuildingName")
        var buildingName : String? = null,

        @SerializedName("RoomId")
        var roomId: Int? = null,

        @SerializedName("BuildingId")
        var buildingId: Int? = null,

        @SerializedName("Amenities")
        var amenities : List<String>? = null,

        @SerializedName("Place")
        var place : String? = null
)
